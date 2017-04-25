package com.view.panel.smallPanel;

import com.util.GBC;

import javax.swing.*;
import java.awt.*;

import static com.SUtil.getDimension;

/**
 * Created by caiyong on 2016/12/24.
 */
public class SOperateRecordPnl extends JPanel
{
    private Component parentWin;
    private Dimension parentDimension;


    public SOperateRecordPnl(Component parentWin)
    {
        setBackground(Color.gray);
        this.parentWin = parentWin;
        parentDimension = parentWin.getSize();
        setDimension();
        buildGUI();

    }

    private void setDimension()
    {
        setSize(getDimension(parentDimension, 1.0, 0.4));
    }


    private void buildGUI()
    {
        setLayout(new GridBagLayout());
        SOperateStatisticTablePnl statisTablePnl = new SOperateStatisticTablePnl(this);
        SStatisticInfoPnl statisInfoPnl = new SStatisticInfoPnl(this);

        add(statisTablePnl, new GBC(0, 0).setWeight(10,1).setIpad(10, 10).setFill(GBC.BOTH));
        add(statisInfoPnl, new GBC(1, 0).setWeight(100,1).setIpad(130, 10).setFill(GBC.BOTH));
    }

}
