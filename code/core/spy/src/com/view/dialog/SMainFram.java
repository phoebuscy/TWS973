package com.view.dialog;

import com.utils.SUtil;
import com.view.panel.STopoFramContentPnl;

import javax.swing.JFrame;
import java.awt.Dimension;

/**
 * Created by 123 on 2016/12/18.
 */
public class SMainFram extends JFrame
{
    public static void main(String[] args)
    {
        SMainFram sMainFram = new SMainFram();
        sMainFram.setVisible(true);
    }

    public SMainFram()
    {
        buildTopoFrame();
    }

    private void buildTopoFrame()
    {
        Dimension guiDim = SUtil.getGUIDimension((double) 4 / 5, (double) 4 / 5);
        setSize(guiDim);
        SUtil.showInScreenCenter(this, guiDim);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(new STopoFramContentPnl(this));
        SUtil.setWindosStyle(this, 1);

    }


}
