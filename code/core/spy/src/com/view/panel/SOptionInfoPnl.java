package com.view.panel;

import com.util.GBC;
import com.view.panel.smallPanel.SConnectPnl;
import com.view.panel.smallPanel.SOptionPanel;
import com.view.panel.smallPanel.SSymbolePanel;

import javax.swing.*;
import java.awt.*;

import static com.SUtil.getDimension;

/**
 * Created by 123 on 2016/12/18.
 */
public class SOptionInfoPnl extends JPanel
{
    private Component parentWin;
    private Dimension parentDimension;

    public SOptionInfoPnl(Component parentWin)
    {
        // setBackground(Color.blue);
        this.parentWin = parentWin;
        parentDimension = parentWin.getSize();
        setDimension();
        buildGUI();
    }

    private void setDimension()
    {
        setSize(getDimension(parentDimension,1.0,0.48));
    }

    private void buildGUI()
    {
        setLayout(new GridBagLayout());

        SConnectPnl sConnectPnl = new SConnectPnl(this);
        SSymbolePanel sSymbolePanel = new SSymbolePanel(this);
        SOptionPanel sOptionPanel = new SOptionPanel(this);

        add(sConnectPnl, new GBC(0, 0).setAnchor(GBC.WEST).setIpad(100, 5).setWeight(10, 1).setFill(GBC.HORIZONTAL));
        add(sSymbolePanel, new GBC(0, 1).setAnchor(GBC.WEST).setIpad(100, 5).setWeight(10, 0).setFill(GBC.HORIZONTAL));
        add(sOptionPanel, new GBC(0, 2).setAnchor(GBC.WEST).setIpad(100, 30).setWeight(10, 10).setFill(GBC.BOTH));

    }


}
