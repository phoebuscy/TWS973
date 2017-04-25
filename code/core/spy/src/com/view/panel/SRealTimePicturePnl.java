package com.view.panel;

import com.SUtil;

import javax.swing.*;
import java.awt.*;

/**
 * Created by 123 on 2016/12/18.
 */
public class SRealTimePicturePnl extends JPanel
{
    private Component parentWin;

    private Dimension parentDimension;

    public SRealTimePicturePnl(Component parentWin)
    {
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
