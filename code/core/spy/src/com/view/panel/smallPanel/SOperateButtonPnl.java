package com.view.panel.smallPanel;

import com.Cst;
import com.TMbassadorSingleton;
import com.enums.SCallOrPut;
import com.util.GBC;
import net.engio.mbassy.listener.Filter;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.IMessageFilter;
import net.engio.mbassy.subscription.SubscriptionContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static com.SUtil.getDimension;
import static com.SUtil.isIntOrDoubleNumber;
import static com.TIconUtil.getProjIcon;

/**
 * Created by caiyong on 2016/12/24.
 */
public class SOperateButtonPnl extends JPanel
{

    private Component parentWin;
    private Dimension parentDimension;

    private OpenCloseButton callBtn = new OpenCloseButton(SCallOrPut.CALL); //  JButton("CALL 开");
    private ChangeOperateButton callPutChangeBtn = new ChangeOperateButton();
    private OpenCloseButton putBtn = new OpenCloseButton(SCallOrPut.PUT); //JButton("PUT 开");

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
            setText("平/反");
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
        private SCallOrPut callOrPut;
        private double realAdd = 0.0;  // 实际收益
        private double percent = 0.0;   //收益百分比

        private Icon waitIco; // 还没开仓图标
        private Icon middleIco; // 收益为0图标
        private Icon gainLittle; // 盈利
        private Icon gainMore; // 更盈利
        private Icon lossLittle;  // 亏损一点
        private Icon lossMore;    // 亏损较多

        public OpenCloseButton(SCallOrPut callOrPut)
        {
            this.callOrPut = callOrPut;
            init();
        }

        public void setActionListerner()
        {
            addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    initIcon();
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
            setText(callOrPut.toString() + "开仓");
            setActionListerner();
        }

        private void initIcon()
        {
            setIcon(waitIco);
            setText(callOrPut.toString() + "开仓");
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
            String txt = String.format("%.2f  %.2f%% 平", realAdd, percent);
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


}
