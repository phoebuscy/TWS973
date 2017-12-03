package com.view.panel.smallPanel;

import com.commdata.enums.SCallOrPut;
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import net.engio.mbassy.listener.Filter;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.IMessageFilter;
import net.engio.mbassy.subscription.SubscriptionContext;
import static com.utils.SUtil.getDimension;
import static com.utils.SUtil.isIntOrDoubleNumber;
import static com.utils.TFileUtil.getConfigValue;
import static com.utils.TIconUtil.getProjIcon;

/**
 * Created by caiyong on 2016/12/24.
 */
public class SOperateButtonPnl extends JPanel
{

    private Component parentWin;
    private Dimension parentDimension;

    private OpenCloseButton callBtn = new OpenCloseButton(Types.Right.Call); //  JButton("CALL 开");
    private ChangeOperateButton callPutChangeBtn = new ChangeOperateButton();
    private OpenCloseButton putBtn = new OpenCloseButton(Types.Right.Put); //JButton("PUT 开");

    private Symbol symbol = SDataManager.getInstance().getSymbol();

    public SOperateButtonPnl(Component parentWin)
    {
        setBackground(Color.gray);
        this.parentWin = parentWin;
        parentDimension = parentWin.getSize();
        setDimension();
        buildGUI();

        TMbassadorSingleton.getInstance("myfirstBus").subscribe(this);
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


    private class ChangeOperateButton extends JButton
    {
        private Icon changeIco = getProjIcon("change");

        public ChangeOperateButton()
        {
            setIcon(changeIco);
            setText(getConfigValue("ping.and.fan",TConst.CONFIG_I18N_FILE)); // 平/反
            addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                  //  callBtn.setProfit("500.2", "0.25");
                  //  putBtn.setProfit("-23", "-0.11");

                    // test
                    TMbassadorSingleton.getInstance("myfirstBus").publish("price:833.4:0.35:13:0.22");
                }
            });
        }
    }

    //test


    static public class PriceStringFilter implements IMessageFilter<String>
    {
        public boolean accepts(String message, SubscriptionContext context)
        {
             return message.startsWith("price");
           // return notNullAndEmptyStr(message);
        }
    }

    @Handler(filters = {@Filter(PriceStringFilter.class)})
    public void processPriceHandler(String message)
    {
        callBtn.setProfit("111.2", "0.88");
        putBtn.setProfit("33", "0.55");
        int b = 1;
    }

    private class OpenCloseButton extends JButton
    {
        private Types.Right right;
        private double realAdd = 0.0;  // 实际收益
        private double percent = 0.0;   //收益百分比

        private Icon waitIco; // 还没开仓图标
        private Icon middleIco; // 收益为0图标
        private Icon gainLittle; // 盈利
        private Icon gainMore; // 更盈利
        private Icon lossLittle;  // 亏损一点
        private Icon lossMore;    // 亏损较多

        public OpenCloseButton(Types.Right right)
        {
            this.right = right;
            init();
        }

        public void setActionListerner()
        {
            addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                 //   initIcon();
                    placeOrder(e); // 下订单
                }
            });
        }

        private void init()
        {
            waitIco =  getProjIcon("img5");     // 还没开仓图标
            middleIco = getProjIcon("img2");    // 收益为0图标
            gainLittle = getProjIcon("img3");   // 盈利
            gainMore = getProjIcon("img4");     // 更盈利
            lossLittle = getProjIcon("img1");   // 亏损一点
            lossMore = getProjIcon("img0");     // 亏损较多
            // “dialog”代表字体，1代表样式(1是粗体，0是平常的）15是字号设置字体
            //price.setFont(new java.awt.Font("Dialog",   1,   15));
            setPreferredSize(new Dimension(100,15));
            setFont(new java.awt.Font("Dialog", 1, 15));
            setIcon(waitIco);
            setText(right.toString() + getConfigValue("begin.buy", TConst.CONFIG_I18N_FILE));
            setActionListerner();
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
                setFaceIcon(percent);
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

    private void placeOrder(ActionEvent e)
    {
        Object source = e.getSource();
        OpenCloseButton openCloseButton = (OpenCloseButton)source;
        if(Types.Right.Call.equals(openCloseButton.right))
        {
            Types.Action action = null;
            Contract contract = symbol.getOrderedCallContract();
            if(contract != null)
            {
                action = Types.Action.SELL;
            }
            else
            {
                contract = symbol.getPrepareOrderCallContract();
                action = Types.Action.BUY;
            }
            action = Types.Action.BUY;
            symbol.placeOrder(contract, action, 100);
        }
        else if(Types.Right.Put.equals(openCloseButton.right))
        {
            Contract putContract = symbol.getPrepareOrderPutContract();
            symbol.placeOrder(putContract, Types.Action.BUY, 100);
        }

        int a = 1;


    }


}
