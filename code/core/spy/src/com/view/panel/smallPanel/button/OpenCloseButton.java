package com.view.panel.smallPanel.button;

import com.commdata.enums.SOpenState;
import com.commdata.pubdata.ContractRealTimeInfo;
import com.dataModel.SDataManager;
import com.dataModel.Symbol;
import com.ib.client.Contract;
import com.ib.client.Types;
import com.utils.Cst;
import com.utils.TConst;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import static com.utils.SUtil.isIntOrDoubleNumber;
import static com.utils.TFileUtil.getConfigValue;
import static com.utils.TIconUtil.getProjIcon;


/**
 * Call 和 Put 的开仓平仓按钮
 */
public class OpenCloseButton extends JButton
{
    private Types.Right right;
    private Contract openContract; // 已经开仓的contract
    private Contract prepareContract; // 准备开仓contract
    private SOpenState openState = SOpenState.NO_OPEN; // 开仓状态
    private int operateCount = 0;

    private Icon waitIco; // 还没开仓图标
    private Icon middleIco; // 收益为0图标
    private Icon gainLittle; // 盈利
    private Icon gainMore; // 更盈利
    private Icon lossLittle;  // 亏损一点
    private Icon lossMore;    // 亏损较多

    private Symbol symbol = SDataManager.getInstance().getSymbol();

    public OpenCloseButton(Types.Right right, Contract openContract)
    {
        this.right = right;
        this.openContract = openContract;
        init();
    }

    public void setPrepareContract(Contract prepareContract)
    {
        this.prepareContract = prepareContract;
        if (prepareContract != null)
        {
            this.right = prepareContract.right();
        }

    }

    public void setOpenContract(Contract openContract)
    {
        this.openContract = openContract;
        this.right = openContract != null ? openContract.right() : Types.Right.None;
    }

    // 初始化开仓状态, 在连接时候，根据profit信息初始化该状态
    public void setOpenState(SOpenState openState)
    {
        this.openState = openState;
    }

    public void initOperateCount(int operateCount)
    {
        this.operateCount = operateCount;
    }

    public void placeOrder()
    {
        if (openState.isOpened())  // 开仓状态
        {
            doPingCang();  // 平仓： 卖 或者 买
        }
        else if(openState.isNoOpen())
        {
            doKaiCang();       // 买
        }
    }

    // 平仓 : 如果是仓位是负数，则是买入操作，如果是正数，则卖出操作
    private void doPingCang()
    {
        if (openContract != null && operateCount != 0)
        {
            Types.Action act = operateCount > 0 ? Types.Action.SELL : Types.Action.BUY;
            openContract.exchange("SMART");
            symbol.placeOrder(openContract, act, operateCount);
            openState = SOpenState.CLOSE_ING;
        }
    }

    // 开仓
    private int doKaiCang()
    {
        if (prepareContract != null)
        {
            openContract = prepareContract.clone();
            openContract.exchange("SMART");
            openContract.primaryExch("AMEX");
            operateCount = getOperateCount(openContract);
            if (operateCount > 0)
            {
                symbol.placeOrder(openContract, Types.Action.BUY, operateCount);
                setButProfitTxt(0D, 0D);
                openState = SOpenState.OPEN_ING;
            }
            else
            {
                JOptionPane.showMessageDialog(this, getConfigValue("get.opt.count.failed", TConst.CONFIG_I18N_FILE));
            }
            return operateCount;
        }
        return 0;
    }

    public void init()
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
                    return (int) Math.rint(symbol.getOnceOperateMoney() / (realTimePrice * 100D));
                }
            }
        }
        return 0;
    }

    public Contract getOpenContract()
    {
        return openContract;
    }
}
