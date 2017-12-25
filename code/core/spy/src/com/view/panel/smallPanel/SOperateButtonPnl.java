package com.view.panel.smallPanel;

import com.commdata.mbassadorObj.MBAPortFolio;
import com.commdata.mbassadorObj.MBAReqIDContractDetails;
import com.commdata.pubdata.ContractRealTimeInfo;
import com.dataModel.SDataManager;
import com.dataModel.Symbol;
import com.ib.client.Contract;
import com.ib.client.Types;
import com.utils.Cst;
import com.utils.GBC;
import com.utils.TConst;
import com.utils.TMbassadorSingleton;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import net.engio.mbassy.listener.Filter;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.IMessageFilter;
import net.engio.mbassy.subscription.SubscriptionContext;
import static com.utils.SUtil.getDimension;
import static com.utils.SUtil.isIntOrDoubleNumber;
import static com.utils.SUtil.isSpyOpt;
import static com.utils.TConst.DATAMAAGER_BUS;
import static com.utils.TConst.REALTIMEPRICEMGR_BUS;
import static com.utils.TConst.SYMBOL_BUS;
import static com.utils.TFileUtil.getConfigValue;
import static com.utils.TIconUtil.getProjIcon;

/**
 * Created by caiyong on 2016/12/24.
 */
public class SOperateButtonPnl extends JPanel
{

    private Component parentWin;
    private Dimension parentDimension;

    private static Contract callContract;
    private static MBAPortFolio callContractPortFolio;
    private static Contract putContract;
    private static MBAPortFolio putContractPortFolio;

    private OpenCloseButton callBtn = new OpenCloseButton(Types.Right.Call, null); //  JButton("CALL 开");
    private OpenCloseButton putBtn = new OpenCloseButton(Types.Right.Put, null); //JButton("PUT 开");
    private ChangeOperateButton callPutChangeBtn = new ChangeOperateButton(callBtn, putBtn);


    private Symbol symbol = SDataManager.getInstance().getSymbol();

    public SOperateButtonPnl(Component parentWin)
    {
        setBackground(Color.gray);
        this.parentWin = parentWin;
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


    /**
     * Call 和 Put 的开仓平仓按钮
     */
    private class OpenCloseButton extends JButton
    {
        private Types.Right right;
        private Contract contract;
        private boolean isOpenState = false; // 是否开仓状态
        private int operateCount = 0;

        private double realAdd = 0.0;  // 实际收益
        private double percent = 0.0;   //收益百分比

        private Icon waitIco; // 还没开仓图标
        private Icon middleIco; // 收益为0图标
        private Icon gainLittle; // 盈利
        private Icon gainMore; // 更盈利
        private Icon lossLittle;  // 亏损一点
        private Icon lossMore;    // 亏损较多

        public OpenCloseButton(Types.Right right, Contract contract)
        {
            this.right = right;
            this.contract = contract;
            init();
        }

        public void setContract(Contract contract)
        {
            this.contract = contract;
            this.right = contract != null? contract.right(): Types.Right.None;
        }

        // 初始化开仓状态, 在连接时候，根据profit信息初始化该状态
        public void initOpenState(boolean isOpenState)
        {
            this.isOpenState = isOpenState;
        }

        public void initOperateCount(int operateCount)
        {
            this.operateCount = operateCount;
        }


        public void placeOrder()
        {
            if (isOpenState)  // 开仓状态
            {
                 doSell();  // 卖
            }
            else
            {
                 doBuy();   // 买
            }
        }

        private void doSell()
        {
            if (contract != null && operateCount > 0)
            {
                symbol.placeOrder(contract, Types.Action.SELL, operateCount);
                isOpenState = false;
            }
        }

        private int doBuy()
        {
            if (contract != null)
            {
                operateCount = getOperateCount(contract);
                symbol.placeOrder(contract, Types.Action.BUY, operateCount);
                setButProfitTxt(0D, 0D);
                isOpenState = true;
                return operateCount;
            }
            return 0;
        }



        private void init()
        {
            waitIco = getProjIcon("img5");     // 还没开仓图标
            middleIco = getProjIcon("img2");    // 收益为0图标
            gainLittle = getProjIcon("img3");   // 盈利
            gainMore = getProjIcon("img4");     // 更盈利
            lossLittle = getProjIcon("img1");   // 亏损一点
            lossMore = getProjIcon("img0");     // 亏损较多
            // “dialog”代表字体，1代表样式(1是粗体，0是平常的）15是字号设置字体
            //price.setFont(new java.awt.Font("Dialog",   1,   15));
            setPreferredSize(new Dimension(100, 15));
            setFont(new java.awt.Font("Dialog", 1, 15));
            setIcon(waitIco);
            setText(right.toString() + getConfigValue("begin.buy", TConst.CONFIG_I18N_FILE));
        }

        private void initIcon()
        {
            setIcon(waitIco);
            setText(right.toString() + getConfigValue("begin.buy", TConst.CONFIG_I18N_FILE));
        }

        public void setProfit(String realAddStr, String percentStr)
        {
            if (isIntOrDoubleNumber(realAddStr) && isIntOrDoubleNumber(percentStr))
            {
                double realAdd = Double.parseDouble(realAddStr);
                double percent = Double.parseDouble(percentStr);
                setButProfitTxt(realAdd, percent * 100D);
            }
            else
            {
                initIcon();
            }
        }

        private void setButProfitTxt(double realAdd, double percent)
        {
            Color color = Color.black;
            if (percent > 0.0)
            {
                color = Cst.ReadColor;
            }
            else if (percent < 0.0)
            {
                color = Cst.GreenColor;
            }
            setFaceIcon(percent);
            setForeground(color);
            String beginClose = getConfigValue("begin.close", TConst.CONFIG_I18N_FILE); // 平
            String txt = String.format("%.2f  %.2f%% %s", realAdd, percent, beginClose);
            setText(txt);
        }

        private void setFaceIcon(double percent)
        {
            Icon icon = middleIco;
            if (percent > 0.0 && percent < 0.05)
            {
                icon = gainLittle;
            }
            else if (percent > 0.05)
            {
                icon = gainMore;
            }
            else if (percent < 0.0 && percent > -0.05)
            {
                icon = lossLittle;
            }
            else if (percent < -0.05)
            {
                icon = lossMore;
            }
            setIcon(icon);
        }
    }


    /**
     * 中间的 平/反 按钮
     */
    private class ChangeOperateButton extends JButton
    {
        private Icon changeIco = getProjIcon("change");
        private OpenCloseButton callBtn;
        private OpenCloseButton putBtn;


        public ChangeOperateButton(OpenCloseButton callBtn, OpenCloseButton putBtn)
        {
            this.callBtn = callBtn;
            this.putBtn = putBtn;
            setIcon(changeIco);
            setText(getConfigValue("ping.and.fan", TConst.CONFIG_I18N_FILE)); // 平/反

        }

        public void doCallPutChange()
        {
            if (callBtn != null)
            {
                callBtn.placeOrder();
            }
            if (putBtn != null)
            {
                putBtn.placeOrder();
            }

        }
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
            if (Types.Right.Call.equals(contract.right()))
            {
                callContract = contract.clone();
            }
            else
            {
                putContract = contract.clone();
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
                return (callContract != null && callContract.conid() == msg.contract.conid()) ||
                       (putContract != null && putContract.conid() == msg.contract.conid());

            }
            return false;
        }
    }

    @Handler(filters = {@Filter(rcvContractRealTimePriceFilter.class)})
    private void processContractRealTimePrice(ContractRealTimeInfo msg)
    {
        // 获取到实时价格， 在获取当前 持仓标的，然后设置button
        if (callContract != null && callContract.conid() == msg.contract.conid())
        {
            if (!callContractPortFolio.isClose()) // 已经平仓的则不处理
            {
                callContractPortFolio.marketPrice = Double.compare(msg.buyPrice, 0D) == 1 ? msg.buyPrice :
                                                    msg.lastPrice;
                callBtn.setProfit(makeYinorkui(callContractPortFolio), makeZdf(callContractPortFolio));
            }
        }
        else if (putContract != null && putContract.conid() == msg.contract.conid())
        {
            if (!putContractPortFolio.isClose()) // 已经平仓的则不处理
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
        if (Types.Right.Call.equals(msg.contract.right()))
        {
            callContractPortFolio = msg;
            callContract = msg.contract.clone();
            callBtn.setProfit(makeYinorkui(msg), makeZdf(msg));
            callBtn.setContract(msg.contract);
            callBtn.initOpenState(true);
            callBtn.initOperateCount((int)Math.rint(msg.position));
            symbol.setOrderedCallContract(callContract, Types.Action.BUY);
        }
        else if (Types.Right.Put.equals(msg.contract.right()))
        {
            putContractPortFolio = msg;
            putContract = msg.contract.clone();
            putBtn.setProfit(makeYinorkui(msg), makeZdf(msg));
            putBtn.setContract(msg.contract);
            putBtn.initOpenState(true);
            putBtn.initOperateCount((int)Math.rint(msg.position));
            symbol.setOrderedCallContract(putContract, Types.Action.BUY);
        }
        // 如果是新的contact，则查询期权实时价格
        queryRealTimePrice(msg);
    }

    // 如果是新的contact，则查询期权实时价格
    private void queryRealTimePrice(MBAPortFolio msg)
    {
        if (msg != null)
        {
            if (Types.Right.Call.equals(msg.contract.right()))
            {
                if (callContract == null || msg.contract.conid() != callContract.conid())
                {
                    symbol.reqRealTimePrice(callContract, msg.contract);
                }
            }
            else if (Types.Right.Put.equals(msg.contract.right()))
            {
                if (putContract == null || msg.contract.conid() != putContract.conid())
                {
                    symbol.reqRealTimePrice(putContract, msg.contract);
                }
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

    private int getOperateCount(Contract contract)
    {
        if (symbol != null)
        {
            ContractRealTimeInfo contractRealTimeInfo = symbol.getContractRealTimeInfo(contract);
            if (contractRealTimeInfo != null)
            {
                double realTimePrice = contractRealTimeInfo.lastPrice;
                if (Double.compare(realTimePrice, 0D) == 1)
                {
                    return (int) Math.rint(symbol.getOnceOperateMoney() / realTimePrice * 100D);
                }
            }
        }
        return 0;
    }


}
