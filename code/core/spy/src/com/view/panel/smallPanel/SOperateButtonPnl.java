package com.view.panel.smallPanel;

import com.commdata.mbassadorObj.MBAReqIDContractDetails;
import com.commdata.mbassadorObj.MBAtickPrice;
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
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import net.engio.mbassy.listener.Filter;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.IMessageFilter;
import net.engio.mbassy.subscription.SubscriptionContext;
import static com.utils.SUtil.getDimension;
import static com.utils.SUtil.isIntOrDoubleNumber;
import static com.utils.TConst.DATAMAAGER_BUS;
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

    private OpenCloseButton callBtn = new OpenCloseButton(Types.Right.Call, null); //  JButton("CALL 开");
    private OpenCloseButton putBtn = new OpenCloseButton(Types.Right.Put, null); //JButton("PUT 开");
    private ChangeOperateButton callPutChangeBtn = new ChangeOperateButton(callBtn, putBtn);

    // 查询买卖价的市场数据reqid和contract的map
    private static Map<Integer, MBAReqIDContractDetails> topMktDataReqID2ContractsMap = new HashMap<>();

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


    static public class priceStringFilter implements IMessageFilter<String>
    {
        public boolean accepts(String message, SubscriptionContext context)
        {
            return message.startsWith("price");
            // return notNullAndEmptyStr(message);
        }
    }

    @Handler(filters = {@Filter(priceStringFilter.class)})
    public void processPriceHandler(String message)
    {
        callBtn.setProfit("111.2", "0.88");
        putBtn.setProfit("33", "0.55");
        int b = 1;
    }


    /**
     * Call 和 Put 的开仓平仓按钮
     */
    private class OpenCloseButton extends JButton
    {
        private Types.Right right;
        private Contract contract;
        private boolean isOpenState = false; // 是否开仓状态

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

        public void SetContract(Contract contract)
        {
            this.contract = contract;
        }

        // 初始化开仓状态, 在连接时候，根据profit信息初始化该状态
        public void initOpenState(boolean isOpenState)
        {
            this.isOpenState = isOpenState;
        }

        public boolean isOpenState()
        {
            return isOpenState;
        }

        public void placeOrder()
        {
            boolean isDo = false;
            if (isOpenState)  // 开仓状态
            {
                isDo = doSell();  // 卖
            }
            else
            {
                isDo = doBuy();   // 买
            }
            if(isDo)
            {
                isOpenState = !isOpenState;
            }
        }

        private boolean doSell()
        {
            Contract contract = isCallBtn() ? symbol.getOrderedCallContract() : symbol.getOrderedPutContract();
            if (contract != null)
            {
                symbol.placeOrder(contract, Types.Action.SELL, 100);
                initIcon();
                return true;
            }
            return false;
        }

        private boolean doBuy()
        {
            Contract contract = isCallBtn() ? symbol.getPrepareOrderCallContract() : symbol.getPrepareOrderPutContract();
            if (contract != null)
            {
                symbol.placeOrder(contract, Types.Action.BUY, 100);
                setButProfitTxt(0D, 0D);
                return true;
            }
            return false;
        }

        private boolean isCallBtn()
        {
            return Types.Right.Call.equals(right);
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
                setButProfitTxt(realAdd, percent);
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
            String txt = String.format("%.3f  %.3f%% %s", realAdd, percent, beginClose);
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
        BigInteger reqid = BigInteger.valueOf(msg.reqid);
        topMktDataReqID2ContractsMap.put(reqid.intValue(), msg);
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
        MBAReqIDContractDetails reqIDContractDetails = topMktDataReqID2ContractsMap.get(msg.tickerId);
        Contract contract = reqIDContractDetails.contractDetails.contract();
        if (contract != null)
        {
            Types.Right right = contract.right();

            // 获取到实时价格， 在获取当前 持仓标的，然后设置button
            if (Types.Right.Call.equals(right))
            {

            }
            else
            {

            }
        }
    }


}
