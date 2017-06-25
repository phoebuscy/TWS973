package com.view.panel.smallPanel;

import com.util.GBC;

import javax.swing.*;
import java.awt.*;

import static com.utils.SUtil.getDimension;

/**
 * Created by 123 on 2016/12/24.
 */
public class SOptionPanel extends JPanel
{

    private Component parentWin;
    private Dimension parentDimension;


    public SOptionPanel(Component parentWin)
    {
        setBackground(Color.gray);
        this.parentWin = parentWin;
        parentDimension = parentWin.getSize();
        setDimension();
        buildGUI();

    }

    private void setDimension()
    {
        setSize(getDimension(parentDimension,1.0,0.7));
    }


    private void buildGUI()
    {
        setLayout(new GridBagLayout());

        SExpireDatePnl expireDatePanel = new SExpireDatePnl(this);
        SOptionLinkTablePnl sOptionLinkTablePnl = new SOptionLinkTablePnl(this);

        add(expireDatePanel,
            new GBC(0, 0).setAnchor(GBC.WEST).setIpad(100, 5).setWeight(10, 2).setFill(GBC.HORIZONTAL));
        add(sOptionLinkTablePnl,
            new GBC(0, 1).setAnchor(GBC.WEST).setIpad(100, 50).setWeight(10, 10).setFill(GBC.BOTH));


    }

}
