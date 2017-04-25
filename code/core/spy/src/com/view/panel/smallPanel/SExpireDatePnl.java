package com.view.panel.smallPanel;

import javax.swing.*;
import java.awt.*;

import static com.SUtil.getDimension;

/**
 * Created by 123 on 2016/12/24.
 */
public class SExpireDatePnl extends JPanel
{

    private Component parentWin;
    private Dimension parentDimension;

    private JLabel expireDate = new JLabel("ExpireDate:");
    private JComboBox expireDataComb = new JComboBox();


    public SExpireDatePnl(Component parentWin)
    {
        setBackground(Color.gray);
        this.parentWin = parentWin;
        parentDimension = parentWin.getSize();
        setDimension();
        setExpireDataComb();
        buildGUI();
    }

    private void setExpireDataComb()
    {
        expireDataComb.addItem("2016/12/25");
        expireDataComb.addItem("2016/12/27");
        expireDataComb.addItem("2016/12/30");
    }

    private void setDimension()
    {
        setSize(getDimension(parentDimension,1.0,0.1));
    }

    private void buildGUI()
    {
        setLayout(new FlowLayout(FlowLayout.LEFT, 15, 0));
        add(expireDate);
        add(expireDataComb);
    }






}
