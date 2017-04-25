package com.view.panel.smallPanel;

import com.util.GBC;

import javax.swing.*;
import java.awt.*;

import static com.SUtil.getDimension;

/**
 * Created by caiyong on 2016/12/24.
 */
public class SOptionDetailPnl extends JPanel
{

    private Component parentWin;
    private Dimension parentDimension;

    public SOptionDetailPnl(Component parentWin)
    {
        setBackground(Color.gray);
        this.parentWin = parentWin;
        parentDimension = parentWin.getSize();
        setDimension();
        buildGUI();

    }

    private void setDimension()
    {
        setPreferredSize(getDimension(parentDimension,0.5,0.4));
    }


    private void buildGUI()
    {
        setLayout(new GridBagLayout());
        SOptionRealTimeInfoPnl callInfoPnl = new SOptionRealTimeInfoPnl(this, 1);
        SOptionRealTimeInfoPnl putInfoPnl = new SOptionRealTimeInfoPnl(this, 2);
        add(callInfoPnl, new GBC(0, 0).setAnchor(GBC.WEST).setIpad(50, 5).setWeight(10, 10).setFill(GBC.BOTH));
        add(putInfoPnl, new GBC(1, 0).setAnchor(GBC.EAST).setIpad(50, 5).setWeight(10, 10).setFill(GBC.BOTH));
    }

}
