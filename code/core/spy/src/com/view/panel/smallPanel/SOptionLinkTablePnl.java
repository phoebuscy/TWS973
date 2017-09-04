package com.view.panel.smallPanel;

import com.table.SOptionLinkTable;
import com.table.TCyTableModel;
import com.utils.GBC;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
                    // test 临时测试代码
                  //    optionLinkTable.updateData(null);

                    Random random = new Random();
                    boolean b = random.nextBoolean();
                    if (b)
                    {
                        TCyTableModel cyTableModel = (TCyTableModel) optionLinkTable.getModel();
                        cyTableModel.removeAllData();
                    }
                    else
                    {
                        optionLinkTable.updateData(null);

                        optionLinkTable.setValueAt(0, 2, b ? 552.8 : 23.3);
                        optionLinkTable.setValueAt(0, 3, b ? 39.8 : 283.3);
                        optionLinkTable.setValueAt(1, 2, b ? 952.8 : 13.3);
                        optionLinkTable.setValueAt(2, 3, b ? 55.8 : 883.3);
                    }
                    //  SDataManager.getInstance().reqHistoryDatas("","","","");
                }
            });
        }
    }


}
