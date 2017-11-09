package com.view.panel.smallPanel;

import com.commdata.mbassadorObj.MBAHistoricalData;
import com.commdata.mbassadorObj.MBAHistoricalDataEnd;
import com.commdata.mbassadorObj.MBAOptionChainMap;
import com.commdata.mbassadorObj.MBAtickPrice;
import com.dataModel.SDataManager;
import com.dataModel.Symbol;
import com.ib.client.ContractDetails;
import com.ib.client.TickType;
import com.ib.client.Types;
import com.table.SOptionLinkTable;
import com.table.TCyTableModel;
import com.utils.GBC;
import com.utils.TConst;
import com.utils.TMbassadorSingleton;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import net.engio.mbassy.listener.Filter;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.IMessageFilter;
import net.engio.mbassy.subscription.SubscriptionContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import static com.utils.SUtil.getBarSizebyDurationSeconds;
import static com.utils.SUtil.getCurrentDayUSAOpenDateTime;
import static com.utils.SUtil.getDimension;
import static com.utils.SUtil.getLastOpenTimeSeconds;
import static com.utils.SUtil.getUSADateTimeByEpochSecond;
import static com.utils.SUtil.ifNowIsOpenTime;
import static com.utils.SUtil.usaChangeToLocalDateTime;
import static com.utils.TConst.DATAMAAGER_BUS;
import static com.utils.TConst.SYMBOL_BUS;
import static com.utils.TFileUtil.getConfigValue;
import static com.utils.TPubUtil.notNullAndEmptyCollection;
import static com.utils.TPubUtil.notNullAndEmptyMap;

/**
 * Created by 123 on 2016/12/24.
 */
public class SOptionLinkTablePnl extends JPanel
{
    private static Logger LogMsg = LogManager.getLogger("datamsg");
    private Component parentWin;
    private Dimension parentDimension;
    private SOptionLinkTable optionLinkTable;
    // 查询买卖价的市场数据reqid和contract的map
    private static Map<Integer, ContractDetails> topMktDataReqID2ContractsMap = new HashMap<>();  //
    // 查询历史数据的reqid和Conracsd Map
    private static Map<Integer, ContractDetails> historicReqID2ContactsMap = new HashMap<>(); //
    // 历史数据保存器
    private Map<Integer, List<MBAHistoricalData>> reqId2historicalDataListMap = new HashMap<>();

    private Symbol symbol = SDataManager.getInstance().getSymbol();


    public JButton testButton = new JButton(getConfigValue("option.table.test", TConst.CONFIG_I18N_FILE)); //"期权表格测试"

    public SOptionLinkTablePnl(Component parentWin)
    {
        setBackground(Color.gray);
        this.parentWin = parentWin;
        parentDimension = parentWin.getSize();
        setDimension();
        buildGUI();
        setTestButtonListener();

        // 订阅消息总线名称为 DATAMAAGER_BUS 的 消息
        TMbassadorSingleton.getInstance(SYMBOL_BUS).subscribe(this);
        TMbassadorSingleton.getInstance(DATAMAAGER_BUS).subscribe(this);
    }

    private void setDimension()
    {
        setSize(getDimension(parentDimension, 1.0, 0.8));
    }

    private void buildGUI()
    {
        optionLinkTable = new SOptionLinkTable();
        JScrollPane scrollPane = new JScrollPane(optionLinkTable);
        optionLinkTable.setFillsViewportHeight(true);

        setLayout(new GridBagLayout());
        add(scrollPane, new GBC(0, 0).setWeight(10, 10).setFill(GBC.BOTH));
        add(testButton, new GBC(0, 1).setIpad(15, 5).setAnchor(GBC.CENTER));
        setPreferredSize(getDimension(parentDimension, 0.7, 0.8));
    }

    private void setTestButtonListener()
    {
        if (testButton != null)
        {
            testButton.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    // test 临时测试代码
                    //    optionLinkTable.updateData(null);
                    Random random = new Random();
                    boolean b = random.nextBoolean();
                    if (b)
                    {
                        TCyTableModel cyTableModel = (TCyTableModel) optionLinkTable.getModel();
                        cyTableModel.removeAllData();
                    }
                    else
                    {
                        optionLinkTable.updateData(null);

                        optionLinkTable.setValueAt(0, 2, b ? 552.8 : 23.3);
                        optionLinkTable.setValueAt(0, 3, b ? 39.8 : 283.3);
                        optionLinkTable.setValueAt(1, 2, b ? 952.8 : 13.3);
                        optionLinkTable.setValueAt(2, 3, b ? 55.8 : 883.3);
                    }
                }
            });
        }
    }


    // 接收处理过后的期权链消息过滤器
    static public class processedOptionChainFilter implements IMessageFilter<MBAOptionChainMap>
    {
        @Override
        public boolean accepts(MBAOptionChainMap msg, SubscriptionContext subscriptionContext)
        {
            return msg != null && notNullAndEmptyMap(msg.getStrike2ContractDtalsLst());
        }
    }

    // 连接消息处理器
    @Handler(filters = {@Filter(processedOptionChainFilter.class)})
    private void setProcessedOptionChain(MBAOptionChainMap msg)
    {
        // 需要实施以下操作
        // 1: 发送取消订阅期权链中各个期权的实时价格
        // 2: 构造新的期权链行数据
        // 3: 订阅期权链中期权的实时价格

        // 取消订阅市场数据
        cancelMarketData();
        // 取消历史数据的请求
        cancelHistoricData();

        // 设置期权链于表格中
        TCyTableModel cyTableModel = (TCyTableModel) optionLinkTable.getModel();
        cyTableModel.removeAllData();
        Map<Double, List<ContractDetails>> optionChainMap = (msg != null) ? msg.getStrike2ContractDtalsLst() : null;
        setOptionChainTable(optionChainMap);

        // 订阅新的期权链中的市场数据
        queryOptionMarketData(optionChainMap);
    }

    private void cancelMarketData()
    {
        if (symbol != null && topMktDataReqID2ContractsMap != null)
        {
            for (Integer reqid : topMktDataReqID2ContractsMap.keySet())
            {
                symbol.cancelMktData(reqid);
            }
            topMktDataReqID2ContractsMap.clear();
        }
    }

    // 取消历史数据的请求
    private void cancelHistoricData()
    {
        if (symbol != null && historicReqID2ContactsMap != null)
        {
            for (Integer reqid : historicReqID2ContactsMap.keySet())
            {
                symbol.cancelReqHistoricalData(reqid);
            }
            historicReqID2ContactsMap.clear();
            if (reqId2historicalDataListMap != null)
            {
                reqId2historicalDataListMap.clear();
            }
        }
    }

    private void setOptionChainTable(Map<Double, List<ContractDetails>> optionChainMap)
    {
        if (optionChainMap != null)
        {
            String callRaise = getConfigValue("call.raise", TConst.CONFIG_I18N_FILE); //CALL涨
            String putDown = getConfigValue("put.down", TConst.CONFIG_I18N_FILE); //PUT跌

            List<Double> strikeLst = new ArrayList<>(optionChainMap.keySet());
            Collections.sort(strikeLst);
            for (Double strike : strikeLst)
            {
                List<ContractDetails> contractDetailsList = optionChainMap.get(strike);
                if (notNullAndEmptyCollection(contractDetailsList) && contractDetailsList.size() == 2)
                {
                    ContractDetails callDts = contractDetailsList.get(0);
                    ContractDetails putDts = contractDetailsList.get(1);
                    if (callDts.contract().right() != Types.Right.Call)
                    {
                        callDts = contractDetailsList.get(1);
                        putDts = contractDetailsList.get(0);
                    }
                    List<Object> callRowData = makeRowData(callRaise, callDts.contract().strike());
                    optionLinkTable.addRowData(callDts, callRowData);
                    List<Object> putRowData = makeRowData(putDown, putDts.contract().strike());
                    optionLinkTable.addRowData(putDts, putRowData);
                }
            }
        }
    }

    private List<Object> makeRowData(Object... arg)
    {
        List<Object> rowData = new ArrayList<>();
        if (arg != null)
        {
            int colCount = optionLinkTable.getColumnModel().getColumnCount();
            for (int i = 0; i < colCount; i++)
            {
                rowData.add(i < arg.length ? arg[i] : "");
            }
        }
        return rowData;
    }

    private void queryOptionMarketData(Map<Double, List<ContractDetails>> optionChainMap)
    {
        topMktDataReqID2ContractsMap.clear();
        historicReqID2ContactsMap.clear();
        reqId2historicalDataListMap.clear();
        //发送订阅实时数据请求,注意需要保留 reqid 和 contrcontract的对应关系，便于获取后更新数据

        if (optionChainMap != null && symbol != null)
        {
            List<ContractDetails> contractDetailsList = new ArrayList<>();
            for (List<ContractDetails> ctrDtsLst : optionChainMap.values())
            {
                contractDetailsList.addAll(ctrDtsLst);
            }

            for (ContractDetails ctrDts : contractDetailsList)
            {
                // 查询期权实时价格
                int reqid = symbol.reqOptionMktData(ctrDts.contract());
                topMktDataReqID2ContractsMap.put(reqid, ctrDts);

                // 查询期权历史价格（最近一次开盘时间）
                if (ifNowIsOpenTime())
                {
                    LocalDateTime openUsaDateTime = getCurrentDayUSAOpenDateTime();
                    LocalDateTime endDatetime = usaChangeToLocalDateTime(openUsaDateTime.plusSeconds(60));
                    String endTimeStr = endDatetime.format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss"));

                    long duration = 60;//  getLastOpenTimeSeconds();// + 60; // 注意，此处要加60秒，是为了获取今天开盘时间的价格
                    String endDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss"));
                    reqid = symbol.reqOptionHistoricDatas(ctrDts.contract(),
                                                          endTimeStr,
                                                          duration,
                                                          Types.DurationUnit.SECOND,
                                                          Types.BarSize._30_secs);
                    if (-1 != reqid)
                    {
                        historicReqID2ContactsMap.put(reqid, ctrDts);
                    }
                }
            }
        }
    }

    // 接收查询symbol的实时价格的消息过滤器
    static public class recvOptionMktDataFilter implements IMessageFilter<MBAtickPrice>
    {
        @Override
        public boolean accepts(MBAtickPrice msg, SubscriptionContext subscriptionContext)
        {
            return topMktDataReqID2ContractsMap.containsKey(msg.tickerId);
        }
    }

    // 实时消息处理器
    @Handler(filters = {@Filter(recvOptionMktDataFilter.class)})
    private void processOptionMktData(MBAtickPrice msg)
    {
        TCyTableModel cyTableModel = (TCyTableModel) optionLinkTable.getModel();
        ContractDetails contractDetails = topMktDataReqID2ContractsMap.get(msg.tickerId);
        int rowIndex = cyTableModel.getRowIndexByUserObj(contractDetails);
        if (rowIndex >= 0)
        {
            TickType tickType = TickType.get(msg.field);
            int colIndex = -1;
            switch (tickType)
            {
                case BID:
                case DELAYED_BID:
                    colIndex = 8;
                    break;
                case ASK:
                case DELAYED_ASK:
                    colIndex = 7;
                    break;
                case LAST:
                case DELAYED_LAST:
                    colIndex = 2;
                    break;
                case OPEN:
                case DELAYED_OPEN:
                    colIndex = 3;
                    break;
                case CLOSE:
                case DELAYED_CLOSE:
                    colIndex = 4;
                    break;
            }
            if (colIndex != -1)
            {
                optionLinkTable.setValueAt(rowIndex, colIndex, msg.price);
            }
        }
    }

    // 接收历史数据消息过滤器
    static public class historicDataFilter implements IMessageFilter<MBAHistoricalData>
    {
        @Override
        public boolean accepts(MBAHistoricalData msg, SubscriptionContext subscriptionContext)
        {
            return historicReqID2ContactsMap.containsKey(msg.reqId);
        }
    }

    @Handler(filters = {@Filter(historicDataFilter.class)})
    private void getHistoricalData(MBAHistoricalData msg)
    {
        List<MBAHistoricalData> hisDatalst = reqId2historicalDataListMap.get(msg.reqId);
        if (hisDatalst == null)
        {
            hisDatalst = new ArrayList<>();
        }
        hisDatalst.add(msg);
        reqId2historicalDataListMap.put(msg.reqId, hisDatalst);
    }

    // 接收历史数据消息结束
    static public class historicDataEndFilter implements IMessageFilter<MBAHistoricalDataEnd>
    {
        @Override
        public boolean accepts(MBAHistoricalDataEnd msg, SubscriptionContext subscriptionContext)
        {
            return historicReqID2ContactsMap.containsKey(msg.reqId);
        }
    }

    // 接收历史数据结束后的处理：取出开盘时间的开盘价格,填入到表格中
    @Handler(filters = {@Filter(historicDataEndFilter.class)})
    private void processHistoricDataEnd(MBAHistoricalDataEnd msg)
    {
        // 取消获取历史数据申请
        symbol.cancelReqHistoricalData(msg.reqId);
        List<MBAHistoricalData> historicalDataList = reqId2historicalDataListMap.get(msg.reqId);

        if (historicalDataList != null)
        {
            ContractDetails contractDetails = historicReqID2ContactsMap.get(msg.reqId);
            // 获取到开始时间的开盘价
            TCyTableModel cyTableModel = (TCyTableModel) optionLinkTable.getModel();
            int rowIndex = cyTableModel.getRowIndexByUserObj(contractDetails);
            if (rowIndex >= 0)
            {
                int colIndex = 3; // 第3列是 ‘开盘价’
                Double openPrice = getCurrentDayOpenPrice(historicalDataList);
                optionLinkTable.setValueAt(rowIndex, colIndex, openPrice);
            }
        }
    }

    // 从当前历史数据中获取开盘价
    private Double getCurrentDayOpenPrice(List<MBAHistoricalData> historicalDataList)
    {
        if (notNullAndEmptyCollection(historicalDataList))
        {
            LocalDateTime todayOpenDateTime = getCurrentDayUSAOpenDateTime();
            for (MBAHistoricalData hisData : historicalDataList)
            {
                LocalDateTime usaDateTime = getUSADateTimeByEpochSecond(hisData.date);

                LogMsg.info("REQ_historicID: " + hisData.reqId + "  Time: " + usaDateTime.toString() + " price: " +
                            hisData.open);

                if (todayOpenDateTime.equals(usaDateTime) || (usaDateTime.isAfter(todayOpenDateTime.plusSeconds(-1)) &&
                                                              usaDateTime.isBefore(todayOpenDateTime.plusSeconds(50))))
                {
                    return hisData.open;
                }
            }
        }
        return 0D;
    }


}
