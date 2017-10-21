package com.view.panel;

import com.utils.GBC;
import com.utils.SUtil;
import com.view.panel.smallPanel.SRealTimePnl;

import javax.swing.*;
import java.awt.*;

/**
 * Created by 123 on 2016/12/18.
 */
public class SRealTimePicturePnl extends JPanel
{
    private Component parentWin;
    private SRealTimePnl sSpyRealTimePnl = new SRealTimePnl("spy", new Dimension(100,200));
    private SRealTimePnl sCallRealTimePnl = new SRealTimePnl("Call", new Dimension(100,150));
    private SRealTimePnl sPutRealTimePnl = new SRealTimePnl("Put", new Dimension(100,150));

    private Dimension parentDimension;

    public SRealTimePicturePnl(Component parentWin)
    {
        setLayout(new GridBagLayout());
        add(sSpyRealTimePnl, new GBC(0,0).setWeight(50,50).setFill(GBC.BOTH));
        add(sCallRealTimePnl, new GBC(0,1).setWeight(50,10).setFill(GBC.BOTH));
        add(sPutRealTimePnl, new GBC(0,2).setWeight(50,10).setFill(GBC.BOTH));


     //   setBackground(Color.cyan);
        this.parentWin = parentWin;
        parentDimension = parentWin.getSize();
        setDimension();
    }

    private void setDimension()
    {
        setSize(SUtil.getDimension(parentDimension, 0.5, 1.0));
    }

}
