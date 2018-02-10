package com.view.panel.smallPanel;

import com.commdata.enums.SOpenState;
import com.commdata.mbassadorObj.MBAPortFolio;
import com.commdata.mbassadorObj.MBAReqIDContractDetails;
import com.commdata.pubdata.ContractRealTimeInfo;
import com.dataModel.SDataManager;
import com.dataModel.Symbol;
import com.ib.client.Contract;
import com.ib.client.Types;
import com.utils.GBC;
import com.utils.TMbassadorSingleton;
import com.view.panel.smallPanel.button.ChangeOperateButton;
import com.view.panel.smallPanel.button.OpenCloseButton;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import javax.swing.JPanel;
import net.engio.mbassy.listener.Filter;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.IMessageFilter;
import net.engio.mbassy.subscription.SubscriptionContext;
import static com.utils.SUtil.getDimension;
import static com.utils.SUtil.isSpyOpt;
import static com.utils.TConst.DATAMAAGER_BUS;
import static com.utils.TConst.REALTIMEPRICEMGR_BUS;
import static com.utils.TConst.SYMBOL_BUS;
import static com.utils.TPubUtil.isCall;
import static com.utils.TPubUtil.isPut;
import static com.utils.TPubUtil.isSameContractID;

/**
 * Created by caiyong on 2016/12/24.
 */
public class SOperateButtonPnl extends JPanel
{

    private Dimension parentDimension;

    private static MBAPortFolio callContractPortFolio;
    private static MBAPortFolio putContractPortFolio;

    private static OpenCloseButton callBtn = new OpenCloseButton(Types.Right.Call, null);    // JButton("CALL 开");
    private static OpenCloseButton putBtn = new OpenCloseButton(Types.Right.Put, null);      // JButton("PUT 开");
    private ChangeOperateButton callPutChangeBtn = new ChangeOperateButton(callBtn, putBtn);


    private Symbol symbol = SDataManager.getInstance().getSymbol();

    public SOperateButtonPnl(Component parentWin)
    {
        setBackground(Color.gray);
        parentDimension = parentWin.getSize();
        setDimension();
        buildGUI();
        setActtionListerner();

        // 订阅消息总线名称为 DATAMAAGER_BUS 的 消息
        TMbassadorSingleton.getInstance(SYMBOL_BUS).subscribe(this);
        TMbassadorSingleton.getInstance(DATAMAAGER_BUS).subscribe(this);
        TMbassadorSingleton.getInstance(REALTIMEPRICEMGR_BUS).subscribe(this);
    }

    private void setActtionListerner()
    {
        callPutChangeBtn.addActionListener(e -> callPutChangeBtn.doCallPutChange());
        callBtn.addActionListener(e -> callBtn.placeOrder());
        putBtn.addActionListener(e -> putBtn.placeOrder());
    }

    private void setDimension()
    {
        setPreferredSize(getDimension(parentDimension, 0.5, 0.2));
    }


    private void buildGUI()
    {
        setLayout(new GridBagLayout());
        add(callBtn, new GBC(0, 0).setAnchor(GBC.WEST).setIpad(10, 10).setInsets(0, 20, 0, 30).setWeight(1, 10)
                                  .setFill(GBC.BOTH));
        add(callPutChangeBtn, new GBC(1, 0).setAnchor(GBC.WEST).setIpad(10, 10).setInsets(0, 20, 0, 30).setWeight(1, 10)
                                           .setFill(GBC.BOTH));
        add(putBtn, new GBC(2, 0).setAnchor(GBC.WEST).setIpad(10, 10).setInsets(0, 20, 0, 30).setWeight(1, 10)
                                 .setFill(GBC.BOTH));
    }

    // 接收双击期权实时信息table的行信息过滤方法
    static public class recvOptRealTimePriceTableDoubleClickInfo implements IMessageFilter<MBAReqIDContractDetails>
    {
        @Override
        public boolean accepts(MBAReqIDContractDetails msg, SubscriptionContext subscriptionContext)
        {
            return msg != null;
        }
    }

    // 处理双击期权表得到的期权信息
    @Handler(filters = {@Filter(recvOptRealTimePriceTableDoubleClickInfo.class)})
    private void processDoubleClickOptTableInfo(MBAReqIDContractDetails msg)
    {
        Contract contract = msg.contract;
        if (contract != null)
        {
            if (isCall(contract))
            {
                callBtn.setPrepareContract(contract);
            }
            else if (isPut(contract))
            {
                putBtn.setPrepareContract(contract);
            }
        }
    }

    // symbolContract 实时价格过滤器
    public static class rcvContractRealTimePriceFilter implements IMessageFilter<ContractRealTimeInfo>
    {
        @Override
        public boolean accepts(ContractRealTimeInfo msg, SubscriptionContext subscriptionContext)
        {
            if (msg != null && msg.contract != null)
            {
                return isSameContractID(callBtn.getOpenContract(), msg.contract) ||
                       isSameContractID(putBtn.getOpenContract(), msg.contract);
            }
            return false;
        }
    }

    @Handler(filters = {@Filter(rcvContractRealTimePriceFilter.class)})
    private void processContractRealTimePrice(ContractRealTimeInfo msg)
    {
        // 获取到实时价格， 在获取当前 持仓标的，然后设置button
        if (isSameContractID(callBtn.getOpenContract(), msg.contract))
        {
            if (callContractPortFolio != null && !callContractPortFolio.isClose()) // 已经平仓的则不处理
            {
                callContractPortFolio.marketPrice = Double.compare(msg.buyPrice, 0D) == 1 ? msg.buyPrice :
                                                    msg.lastPrice;
                callBtn.setProfit(makeYinorkui(callContractPortFolio), makeZdf(callContractPortFolio));
            }
        }
        else if (isSameContractID(putBtn.getOpenContract(), msg.contract))
        {
            if (putContractPortFolio != null && !putContractPortFolio.isClose()) // 已经平仓的则不处理
            {
                putContractPortFolio.marketPrice = Double.compare(msg.buyPrice, 0D) == 1 ? msg.buyPrice : msg.lastPrice;
                putBtn.setProfit(makeYinorkui(putContractPortFolio), makeZdf(putContractPortFolio));
            }
        }
    }

    // 接收账户信息过滤器 : 只显示SPY的OPT
    static public class portFolioDataFilter implements IMessageFilter<MBAPortFolio>
    {
        @Override
        public boolean accepts(MBAPortFolio msg, SubscriptionContext subscriptionContext)
        {
            return msg != null && isSpyOpt(msg);
        }
    }

    @Handler(filters = {@Filter(portFolioDataFilter.class)})
    private void processPortFolioData(MBAPortFolio msg)
    {
        if (msg.isClose()) // 处理已平仓contract
        {
            processClosedContract(msg);
        }
        else // 处理未平仓contract
        {
            processOpenedContract(msg);
        }
        // 如果是新的contact，则查询期权实时价格
        queryRealTimePrice(msg);
    }

    // 如果是新的contact，则查询期权实时价格
    private void queryRealTimePrice(MBAPortFolio msg)
    {
        if (msg != null)
        {
            Contract openedContract = isCall(msg.contract) ? callBtn.getOpenContract() : putBtn.getOpenContract();
            if (openedContract != null && !isSameContractID(openedContract, msg.contract))
            {
                symbol.reqRealTimePrice(openedContract, msg.contract);
            }
        }
    }

    private String makeZdf(MBAPortFolio msg) // 涨跌幅
    {
        if (msg != null && Double.compare(msg.position, 0D) == 1)
        {
            double averavePrice = msg.averageCost / 100D;
            double diff = msg.marketPrice - averavePrice;
            Double percent = diff / averavePrice;
            return String.format("%.2f", percent);
        }
        return "";
    }

    private String makeYinorkui(MBAPortFolio msg)  // 盈亏金额
    {
        if (msg != null)
        {
            if (msg.isClose()) // 平仓了的用 realizedPNL
            {
                return String.format("%.1f", msg.realizedPNL);
            }
            else // 未平仓的需要计算
            {
                return String.format("%.1f", (msg.marketPrice - (msg.averageCost / 100D)) * msg.position * 100);
            }
        }
        return "";
    }


    // 处理已平仓contract
    private void processClosedContract(MBAPortFolio portFolio)
    {
        if (portFolio != null && portFolio.contract != null)
        {
            if (isCall(portFolio.contract) && callContractPortFolio != null &&
                isSameContractID(callContractPortFolio.contract, portFolio.contract))
            {
                callContractPortFolio = null;
                callBtn.setOpenState(SOpenState.NO_OPEN);
                callBtn.init();
            }
            else if (isPut(portFolio.contract) && putContractPortFolio != null &&
                     isSameContractID(putContractPortFolio.contract, portFolio.contract))
            {
                putContractPortFolio = null;
                putBtn.setOpenState(SOpenState.NO_OPEN);
                putBtn.init();
            }
        }
    }

    // 处理未平仓contract
    private void processOpenedContract(MBAPortFolio portFolio)
    {
        if (portFolio != null && portFolio.contract != null)
        {
            if (isCall(portFolio.contract) && callContractPortFolio != null &&
                isSameContractID(callBtn.getOpenContract(), portFolio.contract))
            {
                callContractPortFolio = portFolio;
                callBtn.setProfit(makeYinorkui(portFolio), makeZdf(portFolio));
                callBtn.setOpenContract(portFolio.contract);
                callBtn.setOpenState(SOpenState.OPENED);
                callBtn.initOperateCount((int) Math.rint(portFolio.position));

            }
            else if (isPut(portFolio.contract) && putContractPortFolio != null &&
                     isSameContractID(putBtn.getOpenContract(), portFolio.contract))
            {
                putContractPortFolio = portFolio;
                putBtn.setProfit(makeYinorkui(portFolio), makeZdf(portFolio));
                putBtn.setOpenContract(portFolio.contract);
                putBtn.setOpenState(SOpenState.OPENED);
                putBtn.initOperateCount((int) Math.rint(portFolio.position));
            }
        }
    }


}
