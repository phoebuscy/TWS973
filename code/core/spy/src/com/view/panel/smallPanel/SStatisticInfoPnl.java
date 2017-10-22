package com.view.panel.smallPanel;

import com.utils.Cst;
import com.utils.GBC;

import com.utils.TConst;
import javax.swing.*;
import java.awt.*;

import static com.utils.SUtil.isIntNumeric;
import static com.utils.SUtil.isIntOrDoubleNumber;
import static com.utils.TFileUtil.getConfigValue;

/**
 * Created by caiyong on 2016/12/25.
 */
public class SStatisticInfoPnl extends JPanel
{
    private Component parentWin;
    private Dimension parentDimension;
    private JLabel operateCountLabel = new JLabel("0");
    private JLabel profitLabel = new JLabel("0.0");


    public SStatisticInfoPnl(Component parentWin)
    {
       // setBackground(Color.gray);
        this.parentWin = parentWin;
        parentDimension = parentWin.getSize();
        setDimension();
        buildGUI();

        //设置操作次数和浮动盈利
        setOperateCountLabel("12");
        setProfitTxt("2233.3");

    }

    private void setDimension()
    {
        setPreferredSize(new Dimension(50, 20));
    }

    private void buildGUI()
    {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder(getConfigValue("statistics",TConst.CONFIG_I18N_FILE))); // 统计

        JLabel label1 = new JLabel(getConfigValue("cur.day.operate.count",TConst.CONFIG_I18N_FILE));//当日操作次数
        JLabel label2 = new JLabel(getConfigValue("cur.day.fd.provice", TConst.CONFIG_I18N_FILE)); //"当日浮动盈利
        label1.setFont(new Font("Dialog", 1, 15));
        label2.setFont(new Font("Dialog", 1, 15));
        operateCountLabel.setFont(new Font("Dialog", 1, 15));
        profitLabel.setFont(new Font("Dialog", 1, 15));

        add(label1, new GBC(0, 0).setWeight(1, 10).setAnchor(GBC.WEST).setInsets(5, 5, 10, 5));
        add(operateCountLabel, new GBC(1, 0).setAnchor(GBC.WEST).setIpad(50, 5).setWeight(10, 2)
                                            .setFill(GBC.HORIZONTAL));
        add(label2, new GBC(0, 1).setWeight(1, 10).setAnchor(GBC.WEST).setInsets(5, 5, 10, 5));
        add(profitLabel, new GBC(1, 1).setAnchor(GBC.WEST).setIpad(50, 5).setWeight(10, 2).setFill(GBC.BOTH));
    }

    public void setOperateCountLabel(String operateCount)
    {
        if (isIntNumeric(operateCount))
        {
            operateCountLabel.setText(operateCount);
        }
    }

    public void setProfitTxt(String profitTxt)
    {
        if (isIntOrDoubleNumber(profitTxt))
        {
            double profit = Double.parseDouble(profitTxt);
            Color color = Color.black;
            if (profit > 0.0)
            {
                color = Cst.ReadColor;
            }
            else if (profit < 0.0)
            {
                color = Cst.GreenColor;
            }
            profitLabel.setForeground(color);
            String txt = String.format("%.2f", profit);
            profitLabel.setText(txt);
        }
    }

}
