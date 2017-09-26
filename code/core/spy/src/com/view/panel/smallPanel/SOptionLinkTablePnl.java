package com.view.panel.smallPanel;

import com.dataModel.SDataManager;
import com.dataModel.Symbol;
import com.dataModel.mbassadorObj.MBAOptionChainMap;
import com.dataModel.mbassadorObj.MBAtickPrice;
import com.ib.client.ContractDetails;
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
import static com.utils.SUtil.getDimension;
import static com.utils.TConst.DATAMAAGER_BUS;
import static com.utils.TFileUtil.getConfigValue;
import static com.utils.TPubUtil.notNullAndEmptyCollection;
import static com.utils.TPubUtil.notNullAndEmptyMap;

/**
 * Created by 123 on 2016/12/24.
 */
public class SOptionLinkTablePnl extends JPanel
{
    private Component parentWin;
    private Dimension parentDimension;
    private SOptionLinkTable optionLinkTable;
    private static Map<Integer, ContractDetails> reqID2ContractsMap = new HashMap<>();  // 查询买卖价的市场数据reqid和contract的map

    public JButton testButton = new JButton("期权表格测试");

    public SOptionLinkTablePnl(Component parentWin)
    {
        setBackground(Color.gray);
        this.parentWin = parentWin;
        parentDimension = parentWin.getSize();
        setDimension();
        buildGUI();
        setTestButtonListener();

        // 订阅消息总线名称为 DATAMAAGER_BUS 的 消息
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
                    //  SDataManager.getInstance().reqHistoryDatas("","","","");
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
        if (reqID2ContractsMap != null)
        {
            Symbol symbol = SDataManager.getInstance().getSymbol();
            for (Integer reqid : reqID2ContractsMap.keySet())
            {
                if (symbol != null)
                {
                    symbol.cancelMktData(reqid);
                }
            }
            reqID2ContractsMap.clear();
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
        reqID2ContractsMap.clear();
        //发送订阅实时数据请求,注意需要保留 reqid 和 contrcontract的对应关系，便于获取后更新数据
        Symbol symbol = SDataManager.getInstance().getSymbol();
        if (optionChainMap != null && symbol != null)
        {
            List<ContractDetails> contractDetailsList = new ArrayList<>();
            for (List<ContractDetails> ctrDtsLst : optionChainMap.values())
            {
                contractDetailsList.addAll(ctrDtsLst);
            }

            for (ContractDetails ctrDts : contractDetailsList)
            {
                int reqid = symbol.reqOptionMktData(ctrDts.contract());
                reqID2ContractsMap.put(reqid, ctrDts);
            }
        }
    }

    // 接收查询symbol的实时价格的消息过滤器
    static public class recvOptionMktDataFilter implements IMessageFilter<MBAtickPrice>
    {
        @Override
        public boolean accepts(MBAtickPrice msg, SubscriptionContext subscriptionContext)
        {
            return reqID2ContractsMap.containsKey(msg.tickerId);
        }
    }

    // 连接消息处理器
    @Handler(filters = {@Filter(recvOptionMktDataFilter.class)})
    private void processOptionMktData(MBAtickPrice msg)
    {
        TCyTableModel cyTableModel = (TCyTableModel) optionLinkTable.getModel();
        ContractDetails contractDetails = reqID2ContractsMap.get(msg.tickerId);
        int rowIndex = cyTableModel.getRowIndexByUserObj(contractDetails);
        if(rowIndex >= 0)
        {
            if(msg.field == 1) // 买价
            {
                optionLinkTable.setValueAt(rowIndex, 6, msg.price);
            }
            else if(msg.field == 2) // 卖价
            {
                optionLinkTable.setValueAt(rowIndex, 5, msg.price);
            }
        }
    }


}
