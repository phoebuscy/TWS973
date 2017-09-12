package com.view.panel.smallPanel;

import com.utils.TConst;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import static com.utils.SUtil.getDimension;
import static com.utils.TFileUtil.getConfigValue;

/**
 * Created by 123 on 2016/12/24.
 */
public class SExpireDatePnl extends JPanel
{

    private Component parentWin;
    private Dimension parentDimension;

    private JLabel expireDate = new JLabel("ExpireDate:");
    private JComboBox expireDataComb = new JComboBox();
    private JButton queryOptionbtn = new JButton(getConfigValue("query.option.chain", TConst.CONFIG_I18N_FILE)); // ��ѯ��Ȩ��


    public SExpireDatePnl(Component parentWin)
    {
        setBackground(Color.gray);
        this.parentWin = parentWin;
        parentDimension = parentWin.getSize();
        setDimension();
        setExpireDataComb();
        buildGUI();
        addActionListener();
    }

    private void addActionListener()
    {
        queryOptionbtn.addActionListener(e ->
        {
            queryOptionChain();
        });

        expireDataComb.addItemListener(e ->
        {
            onExpireDateChanged(e);
        });

    }

    private void onExpireDateChanged(ItemEvent e)
    {

    }

    private void queryOptionChain()
    {

    }

    private void setExpireDataComb()
    {
        expireDataComb.addItem("2016/12/25");
        expireDataComb.addItem("2016/12/27");
        expireDataComb.addItem("2016/12/30");
    }

    private void setDimension()
    {
        setSize(getDimension(parentDimension, 1.0, 0.1));
    }

    private void buildGUI()
    {
        setLayout(new FlowLayout(FlowLayout.LEFT, 15, 0));
        add(expireDate);
        add(expireDataComb);
        add(queryOptionbtn);
    }


}
