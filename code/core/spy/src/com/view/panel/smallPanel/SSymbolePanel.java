package com.view.panel.smallPanel;

import javax.swing.*;
import java.awt.*;

import static com.SUtil.getDimension;

/**
 * Created by 123 on 2016/12/24.
 */
public class SSymbolePanel extends JPanel
{

    private Component parentWin;
    private Dimension parentDimension;

    private JLabel symbol = new JLabel("Symbol:");
    private JTextField symbolText = new JTextField("spy", 10);
    private JLabel price = new JLabel("225.71    +0.33    +0.15%:");


    public SSymbolePanel(Component parentWin)
    {
        setBackground(Color.white);
        this.parentWin = parentWin;
        parentDimension = parentWin.getSize();
        setDimension();
        buildGUI();

        setPrice(257.8888, 3.2678);
    }

    private void setDimension()
    {
        setSize(getDimension(parentDimension,1.0,0.1));
    }


    private void buildGUI()
    {
        setLayout(new FlowLayout(FlowLayout.LEFT, 15, 0));
        add(symbol);
        add(symbolText);
        add(price);
    }

    public void setPrice(double currentPrice, double add)
    {
        boolean addFlag = (add > 0.0)? true:false;
        String str = String.format("%.2f   %.2f   %.2f%%", currentPrice, add,add/currentPrice * 100);
        price.setText(str);
        // “dialog”代表字体，1代表样式(1是粗体，0是平常的）15是字号设置字体
        //price.setFont(new java.awt.Font("Dialog",   1,   15));
        price.setForeground(addFlag? Color.RED: Color.blue);
    }


}
