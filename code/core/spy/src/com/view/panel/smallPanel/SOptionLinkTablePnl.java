package com.view.panel.smallPanel;

import com.dataModel.SDataManager;
import com.table.SOptionLinkTable;
import com.utils.GBC;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static com.utils.SUtil.getDimension;

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
                    // optionLinkTable.updateData(null);
                    // test 临时测试代码
                    SDataManager.getInstance().reqHistoryDatas("","","","");

                }
            });
        }
    }


}
