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
 * Call �� Put �Ŀ���ƽ�ְ�ť
 */
public class OpenCloseButton extends JButton
{
    private Types.Right right;
    private Contract openContract; // �Ѿ����ֵ�contract
    private Contract prepareContract; // ׼������contract
    private SOpenState openState = SOpenState.NO_OPEN; // ����״̬
    private int operateCount = 0;

    private Icon waitIco; // ��û����ͼ��
    private Icon middleIco; // ����Ϊ0ͼ��
    private Icon gainLittle; // ӯ��
    private Icon gainMore; // ��ӯ��
    private Icon lossLittle;  // ����һ��
    private Icon lossMore;    // ����϶�

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

    // ��ʼ������״̬, ������ʱ�򣬸���profit��Ϣ��ʼ����״̬
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
        if (openState.isOpened())  // ����״̬
        {
            doPingCang();  // ƽ�֣� �� ���� ��
        }
        else if(openState.isNoOpen())
        {
            doKaiCang();       // ��
        }
    }

    // ƽ�� : ����ǲ�λ�Ǹ���������������������������������������
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

    // ����
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
        waitIco = getProjIcon("img5");     // ��û����ͼ��
        middleIco = getProjIcon("img2");    // ����Ϊ0ͼ��
        gainLittle = getProjIcon("img3");   // ӯ��
        gainMore = getProjIcon("img4");     // ��ӯ��
        lossLittle = getProjIcon("img1");   // ����һ��
        lossMore = getProjIcon("img0");     // ����϶�
        // ��dialog���������壬1������ʽ(1�Ǵ��壬0��ƽ���ģ�15���ֺ���������
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
        String beginClose = getConfigValue("begin.close", TConst.CONFIG_I18N_FILE); // ƽ
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
