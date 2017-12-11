package com.view.panel.smallPanel;

import com.commdata.mbassadorObj.MBAHistoricalData;
import com.commdata.mbassadorObj.MBAHistoricalDataEnd;
import com.commdata.mbassadorObj.MBAOptionChainMap;
import com.commdata.mbassadorObj.MBAReqIDContractDetails;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import net.engio.mbassy.listener.Filter;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.IMessageFilter;
import net.engio.mbassy.subscription.SubscriptionContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import static com.utils.SUtil.getCurrentDayUSAOpenDateTime;
import static com.utils.SUtil.getDimension;
import static com.utils.SUtil.getLastOpenTimeSeconds;
import static com.utils.SUtil.getUSADateTimeByEpochSecond;
import static com.utils.SUtil.ifNowIsOpenTime;
import static com.utils.SUtil.isIntOrDoubleNumber;
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
    private static Logger LogApp = LogManager.getLogger("applog");
    private static Logger LogMsg = LogManager.getLogger("datamsg");
    private Component parentWin;
    private Dimension parentDimension;
    private SOptionLinkTable optionLinkTable;
    // 查询买卖价的市场数据reqid和contract的map
    private static Map<Integer, ContractDetails> topMktDataReqID2ContractsMap = new HashMap<>();
    // 查询历史数据的reqid和Conracsd Map
    private static Map<Integer, ContractDetails> historicReqID2ContactsMap = new HashMap<>();
    // 历史数据保存器
    private Map<Integer, List<MBAHistoricalData>> reqId2historicalDataListMap = new HashMap<>();

    private Symbol symbol = SDataManager.getInstance().getSymbol();


    public SOptionLinkTablePnl(Component parentWin)
    {
        setBackground(Color.gray);
        this.parentWin = parentWin;
        parentDimension = parentWin.getSize();
        setDimension();
        buildGUI();
        setTableListener();

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
        setPreferredSize(getDimension(parentDimension, 0.7, 0.8));
    }

    // 设置双击事件处理器，发送选择的 call 和put 给操作面板
    private void setTableListener()
    {
        if (optionLinkTable != null)
        {
            optionLinkTable.addMouseListener(new MouseAdapter()
            {
                @Override
                public void mouseClicked(MouseEvent e)
                {
                    processDoubleClickOptionLinkTable(e);
                }
            });
        }
    }

    private void processDoubleClickOptionLinkTable(MouseEvent event)
    {
        if (event != null && event.getClickCount() == 2)  // 如果是双击则处理
        {
            Object source = event.getSource();
            if (source instanceof SOptionLinkTable)
            {
                int rowIndex = optionLinkTable.getSelectedRow();
                SOptionLinkTable optLinkTable = (SOptionLinkTable) source;
                TCyTableModel tableModel = (TCyTableModel) optLinkTable.getModel();
                Object currentPriceObj = tableModel.getValueAt(rowIndex, 2);
                double currentPrice = isIntOrDoubleNumber(currentPriceObj) ? Double.valueOf(currentPriceObj
                                                                                                    .toString()) : 0D;
                Object todayOpenPriceObj = tableModel.getValueAt(rowIndex, 3);
                double todayOpenPrice = isIntOrDoubleNumber(todayOpenPriceObj) ? Double.valueOf(todayOpenPriceObj
                                                                                                        .toString()) :
                                        0D;
                Object yesterdayClosePriceObj = tableModel.getValueAt(rowIndex, 4);
                double yesterdayClosePrice = isIntOrDoubleNumber(yesterdayClosePriceObj) ? Double.valueOf(
                        yesterdayClosePriceObj.toString()) : 0D;

                Object selectObj = optLinkTable.getRowUserObject(rowIndex);
                if (selectObj instanceof ContractDetails)
                {
                    ContractDetails ctrDtls = (ContractDetails) selectObj;
                    int conid = ctrDtls.contract() != null ? ctrDtls.conid() : -1;
                    for (Map.Entry<Integer, ContractDetails> entry : topMktDataReqID2ContractsMap.entrySet())
                    {
                        ContractDetails contractDetails = entry.getValue();
                        int conid_other = contractDetails.contract() != null ? contractDetails.conid() : -1;
                        // 找到了contractDetail 和 reqid 之后 广播消息
                        if (conid != -1 && conid == conid_other)
                        {
                            // 校验是否有正在下单的对应的 call或put
                            Types.Right right = contractDetails.contract().right();
                            if ((Types.Right.Call.equals(right) && symbol.getOrderedCallContract() != null) ||
                                (Types.Right.Put.equals(right) && symbol.getOrderedPutContract() != null))
                            {
                                JOptionPane.showMessageDialog(this,
                                                              getConfigValue("tip.have.order",
                                                                             TConst.CONFIG_I18N_FILE));
                            }

                            long reqid = entry.getKey();
                            // Symbol发布数据
                            TMbassadorSingleton.getInstance(SYMBOL_BUS).publish(new MBAReqIDContractDetails(reqid,
                                                                                                            contractDetails,
                                                                                                            currentPrice,
                                                                                                            yesterdayClosePrice,
                                                                                                            todayOpenPrice));
                            //  symbol 设置准备进行开仓的contract
                            symbol.setPrepareOrderContract(contractDetails.contract());
                        }
                    }
                }
            }
        }
        int a = 1;
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
                    String endTimeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss"));
                    long duration = getLastOpenTimeSeconds() + 60; // 注意，此处要加60秒，是为了获取今天开盘时间的价格
                    reqid = symbol.reqOptionHistoricDatas_pub(ctrDts.contract(),
                                                              endTimeStr,
                                                              duration,
                                                              Types.DurationUnit.SECOND,
                                                              Types.BarSize._10_secs);

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
            reqId2historicalDataListMap.put(msg.reqId, hisDatalst);
        }
        hisDatalst.add(msg);
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
            MBAHistoricalData nearOpenhisDate = getTheNearestHistoricalData(todayOpenDateTime, historicalDataList);
            if (nearOpenhisDate != null)
            {
                LocalDateTime usaDateTime = getUSADateTimeByEpochSecond(nearOpenhisDate.date);
                if (Math.abs(Duration.between(usaDateTime, todayOpenDateTime).toMinutes()) < 10)
                {
                    return usaDateTime.isBefore(todayOpenDateTime) ? nearOpenhisDate.close : nearOpenhisDate.open;
                }
            }
        }
        else
        {
            LogApp.error("SOptionLinkTablePnl getCurrentDayOpenPrice get openPrice failed");
        }
        return 0D;
    }

    // 获取与指定时间最近的历史数据
    private MBAHistoricalData getTheNearestHistoricalData(LocalDateTime usaOpenTime,
                                                          List<MBAHistoricalData> historicalDataList)
    {
        if (usaOpenTime != null && historicalDataList != null)
        {
            long nearestTime = Integer.MAX_VALUE;
            MBAHistoricalData retMbaHisData = null;
            for (MBAHistoricalData mbaHisData : historicalDataList)
            {
                LocalDateTime usaDateTime = getUSADateTimeByEpochSecond(mbaHisData.date);
                Duration duration = Duration.between(usaOpenTime, usaDateTime);
                long durationSecond = Math.abs(duration.getSeconds());
                if (durationSecond < nearestTime)
                {
                    nearestTime = durationSecond;
                    retMbaHisData = mbaHisData;
                }
            }
            return retMbaHisData;
        }
        return null;
    }


}
