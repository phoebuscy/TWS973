package com.view.panel;

import com.util.GBC;
import com.view.panel.smallPanel.SOperateButtonPnl;
import com.view.panel.smallPanel.SOperateRecordPnl;
import com.view.panel.smallPanel.SOptionDetailPnl;

import javax.swing.*;
import java.awt.*;

import static com.SUtil.getDimension;

/**
 * Created by 123 on 2016/12/18.
 */
public class SOperatePnl extends JPanel
{
    private Component parentWin;

    private Dimension parentDimension;

    public SOperatePnl(Component parentWin)
    {
        // setBackground(Color.gray);
        this.parentWin = parentWin;
        parentDimension = getDimension(parentWin.getSize(),1.0,0.5);
        setDimension();
        buildGUI();
    }

    private void setDimension()
    {
        setSize(getDimension(parentDimension,1.0,0.5));
    }

    private void buildGUI()
    {
        setLayout(new GridBagLayout());
        SOptionDetailPnl optionDetailPanel = new SOptionDetailPnl(this);
        SOperateButtonPnl operateButtonPanel = new SOperateButtonPnl(this);
        SOperateRecordPnl operateRecordPnl = new SOperateRecordPnl(this);

        add(optionDetailPanel, new GBC(0, 0).setAnchor(GBC.WEST).setIpad(100, 13).setWeight(10, 12)
                                            .setFill(GBC.BOTH));
        add(operateButtonPanel, new GBC(0, 1).setAnchor(GBC.WEST).setIpad(100, 10).setWeight(10, 5)
                                             .setFill(GBC.BOTH));
        add(operateRecordPnl, new GBC(0, 2).setAnchor(GBC.WEST).setIpad(100, 13).setWeight(10, 20)
                                           .setFill(GBC.BOTH));


    }

}
