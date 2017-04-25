package com.view.panel.smallPanel;

import com.dataModel.SDataManager;
import com.table.SOptionLinkTable;
import com.util.GBC;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static com.SUtil.getDimension;

/**
 * Created by 123 on 2016/12/24.
 */
public class SOptionLinkTablePnl extends JPanel
{
    private Component parentWin;
    private Dimension parentDimension;

    private SOptionLinkTable optionLinkTable;

    public JButton testButton = new JButton("期权表格测试");

    public SOptionLinkTablePnl(Component parentWin)
    {
        setBackground(Color.gray);
        this.parentWin = parentWin;
        parentDimension = parentWin.getSize();
        setDimension();
        buildGUI();
        setTestButtonListener();
    }

    private void setDimension()
    {
        setSize(getDimension(parentDimension, 1.0, 0.8));
    }

    private void buildGUI()
    {
        // setLayout(new BorderLayout());
        optionLinkTable = new SOptionLinkTable();
        JScrollPane scrollPane = new JScrollPane(optionLinkTable);
        optionLinkTable.setFillsViewportHeight(true);

        setLayout(new GridBagLayout());
        add(scrollPane, new GBC(0, 0).setWeight(10, 10).setFill(GBC.BOTH));
        add(testButton, new GBC(0, 1).setIpad(15, 5).setAnchor(GBC.CENTER));
        setPreferredSize(getDimension(parentDimension, 0.7, 0.8));
    }

    private void setTestButtonListener()
    {
        if (testButton != null)
        {
            testButton.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    optionLinkTable.updateData(null);
                    // test
                    SDataManager.getInstance().disconnect();

                }
            });
        }
    }


}
