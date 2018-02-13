package com.view.panel.smallPanel;

import com.commdata.mbassadorObj.MBAHistoricalData;
import com.commdata.mbassadorObj.MBAOptionChainMap;
import com.commdata.mbassadorObj.MBAReqIDContractDetails;
import com.commdata.pubdata.ContractRealTimeInfo;
import com.commdata.pubdata.ProcessInAWT;
import com.dataModel.SDataManager;
import com.dataModel.Symbol;
import com.ib.client.Contract;
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
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import javafx.util.Pair;
import net.engio.mbassy.listener.Filter;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.IMessageFilter;
import net.engio.mbassy.subscription.SubscriptionContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.utils.SUtil.getCurrentAmericaLocalDateTime;
import static com.utils.SUtil.getDimension;
import static com.utils.SUtil.getLastDayUSAOpenDateTime;
import static com.utils.SUtil.getLocalDateTimeByEpochSecond;
import static com.utils.SUtil.getMBAHistoricalDataByDateTime;
import static com.utils.SUtil.getUSADateTimeByEpochSecond;
import static com.utils.SUtil.getUSAOpenDateTimeByLastDay;
import static com.utils.SUtil.ifNowIsOpenTime;
import static com.utils.SUtil.usaChangeToLocalDateTime;
import static com.utils.TConst.DATAMAAGER_BUS;
import static com.utils.TConst.REALTIMEPRICEMGR_BUS;
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

    private static Map<Integer, Contract> conid2ContractMap = new HashMap<>(); // 存放contract id 和contract 的Map

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
        TMbassadorSingleton.getInstance(REALTIMEPRICEMGR_BUS).subscribe(this);
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
                Object selectObj = optionLinkTable.getRowUserObject(rowIndex);
                if (selectObj instanceof Integer)
                {
                    Contract contract = conid2ContractMap.get(selectObj);
                    // Symbol发布数据
                    TMbassadorSingleton.getInstance(SYMBOL_BUS).publish(new MBAReqIDContractDetails(-1,
                            contract,
                            null,
                            0D,
                            0D,
                            0D));

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

    // 期权链处理器
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

        // 如果不是开盘时间，则通过历史数据获取当前价格和开盘价格
        if (!ifNowIsOpenTime())
        {
            processLastOpenDayHistoryData(optionChainMap);
        }
    }

    // 如果不是开盘时间，则通过历史数据获取当前价格和开盘价格
    private void processLastOpenDayHistoryData(Map<Double, List<ContractDetails>> optionChainMap)
    {
        if (notNullAndEmptyMap(optionChainMap))
        {
            for (Map.Entry<Double, List<ContractDetails>> entry : optionChainMap.entrySet())
            {
                List<ContractDetails> contractDetailsList = entry.getValue();
                if (notNullAndEmptyCollection(contractDetailsList))
                {
                    for (ContractDetails contractDetails : contractDetailsList)
                    {
                        getLastOpenDayHistoryData(contractDetails.contract());
                    }
                }
            }
        }
    }

    // 如果不是开盘时间，则通过历史数据获取当前价格和开盘价格
    private void getLastOpenDayHistoryData(Contract contract)
    {
        // 获取指定天数之前的开盘的本地时间, 参数 lastDay 是表示之前多少天
        Pair<LocalDateTime, LocalDateTime> lastUsaOpenCloseTime = getLastDayUSAOpenDateTime();
        LocalDateTime usaCurDateTime = getCurrentAmericaLocalDateTime();
        if (usaCurDateTime.isBefore(lastUsaOpenCloseTime.getKey()))
        {
            lastUsaOpenCloseTime = getUSAOpenDateTimeByLastDay(1);
        }
        LocalDateTime localCloseDateTime = usaChangeToLocalDateTime(lastUsaOpenCloseTime.getValue());

        long duration = Duration.between(localCloseDateTime, LocalDateTime.now()).toDays();
        duration = duration <= 0 ? 1 : duration;

        Types.BarSize barSize = Types.BarSize._30_mins;
        String locatime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss"));

        // 注意：查询历史数据需要用本地时间
        symbol.getHistoricDatasAndProcess(contract,
                locatime,
                duration,
                Types.DurationUnit.DAY,
                barSize,
                getDataFinishProcess(contract));

    }


    private ProcessInAWT getDataFinishProcess(final Contract contract)
    {
        ProcessInAWT processInAWT = new ProcessInAWT()
        {
            @Override
            public void successInAWT(Object param)
            {
                Pair<LocalDateTime, LocalDateTime> lastUsaOpenCloseTime = getLastDayUSAOpenDateTime();
                LocalDateTime usaOpenTime = lastUsaOpenCloseTime.getKey();
                LocalDateTime usaCloseTime = lastUsaOpenCloseTime.getValue();
                List<MBAHistoricalData> historicalDataList = (List) param;

                // 注意：historicData是本地时间记录
                MBAHistoricalData openTimeHistoricData = getMBAHistoricalDataByDateTime(usaChangeToLocalDateTime(usaOpenTime),
                        historicalDataList);
                LocalDateTime tmpTime = getLocalDateTimeByEpochSecond(openTimeHistoricData.date);
                MBAHistoricalData closeTimeHistoricData = getMBAHistoricalDataByDateTime(usaChangeToLocalDateTime(usaCloseTime),
                        historicalDataList);

                /*
                ContractRealTimeInfo contractRealTimeInfo = null;// contractid2RealPriceMap.get(contract.conid());
                for (MBAHistoricalData historicalData : historicalDataList)
                {
                    LocalDateTime usaDateTime = getUSADateTimeByEpochSecond(historicalData.date);
                    if (openTime.equals(usaDateTime))
                    {
                        if (contractRealTimeInfo != null)
                        {
                            contractRealTimeInfo.todayOpen = historicalData.close;
                            MBAHistoricalData lastHistoricData = historicalDataList.get(historicalDataList.size() - 1);
                            if (lastHistoricData != null)
                            {
                                contractRealTimeInfo.lastPrice = lastHistoricData.close;
                            }
                            // 发布实时价格数据
                            TMbassadorSingleton.getInstance(REALTIMEPRICEMGR_BUS).publish(new ContractRealTimeInfo(
                                    contractRealTimeInfo,
                                    TickType.LAST));
                        }
                    }
                    if (closeTime.equals(usaDateTime) && Double.compare(contractRealTimeInfo.yesterdayClose, 0D) == 0)
                    {
                        contractRealTimeInfo.yesterdayClose = historicalData.close;
                    }
                }
                */
            }

            @Override
            public void failedInAWT(Object param)
            {
                super.failedInAWT(param);
            }
        };
        return processInAWT;
    }


    private void cancelMarketData()
    {
        if (symbol != null)
        {
            for (Contract contract : conid2ContractMap.values())
            {
                symbol.cancelRealTimePrice(contract);
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
                    optionLinkTable.addRowData(callDts.contract().conid(), callRowData);
                    List<Object> putRowData = makeRowData(putDown, putDts.contract().strike());
                    optionLinkTable.addRowData(putDts.contract().conid(), putRowData);
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
        conid2ContractMap.clear();
        if (notNullAndEmptyMap(optionChainMap))
        {
            for (Map.Entry<Double, List<ContractDetails>> entry : optionChainMap.entrySet())
            {
                List<ContractDetails> contractDetailsList = entry.getValue();
                if (notNullAndEmptyCollection(contractDetailsList))
                {
                    for (ContractDetails contractDetails : contractDetailsList)
                    {
                        conid2ContractMap.put(contractDetails.contract().conid(), contractDetails.contract());
                        symbol.reqRealTimePrice(contractDetails.contract());
                    }
                }
            }
        }
    }


    // symbolContract 实时价格过滤器
    public static class rcvContractRealTimePriceFilter implements IMessageFilter<ContractRealTimeInfo>
    {
        @Override
        public boolean accepts(ContractRealTimeInfo msg, SubscriptionContext subscriptionContext)
        {
            return msg != null && conid2ContractMap.containsKey(msg.contract.conid());
        }
    }

    @Handler(filters = {@Filter(rcvContractRealTimePriceFilter.class)})
    private void processContractRealTimePrice(ContractRealTimeInfo msg)
    {
        if (msg != null)
        {
            TCyTableModel cyTableModel = (TCyTableModel) optionLinkTable.getModel();
            int rowIndex = cyTableModel.getRowIndexByUserObj(msg.contract.conid());
            if (rowIndex >= 0)
            {
                optionLinkTable.setValueAt(rowIndex, 2, msg.lastPrice);
                optionLinkTable.setValueAt(rowIndex, 3, msg.todayOpen);
                optionLinkTable.setValueAt(rowIndex, 4, msg.yesterdayClose);
                optionLinkTable.setValueAt(rowIndex, 7, msg.salePrice);
                optionLinkTable.setValueAt(rowIndex, 8, msg.buyPrice);
            }
        }
    }


}
