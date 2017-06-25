package com.view.panel.smallPanel;

import com.model.SOptionTableModel;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;

import static com.utils.SUtil.getDimension;
import static com.utils.SUtil.setColumWidth;

/**
 * Created by 123 on 2016/12/24.
 */
public class SOptionTablePnl extends JPanel
{
    private Component parentWin;
    private Dimension parentDimension;

    private JTable table;
    private TableModel tableModel;

    public SOptionTablePnl(Component parentWin)
    {
        setBackground(Color.gray);
        this.parentWin = parentWin;
        parentDimension = parentWin.getSize();
        setDimension();
        buildGUI();
    }

    private void setDimension()
    {
        setSize(getDimension(parentDimension,1.0,0.8));
    }

    private void buildGUI()
    {
        setLayout(new BorderLayout());
        table = crtTable();
        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);
        add(scrollPane);
        setPreferredSize(getDimension(parentDimension,0.7,0.8));
    }

    private JTable crtTable()
    {
        String[] columnNames = {"行权价", "CALL/PUT","最新价", "涨跌额", "涨跌幅", "卖一", "买一", "成交量", "未平仓"};
        Object[][] data = {
                {255.50, "CALL涨", 2.85, 3.2, 0.18, 2.89, 2.87, 5000, 23436},
                {255.50, "PUT跌",2.85, 3.2, 0.18, 2.89, 2.87, 5000, 23436},
                {256.00, "CALL涨",2.85, 3.2, 0.18, 2.89, 2.87, 5000, 23436},
                {255.50, "CALL涨", 2.85, 3.2, 0.18, 2.89, 2.87, 5000, 23436},
                {255.50, "PUT跌",2.85, 3.2, 0.18, 2.89, 2.87, 5000, 23436},
                {256.00, "CALL涨",2.85, 3.2, 0.18, 2.89, 2.87, 5000, 23436},
                {255.50, "CALL涨", 2.85, 3.2, 0.18, 2.89, 2.87, 5000, 23436},
                {255.50, "PUT跌",2.85, 3.2, 0.18, 2.89, 2.87, 5000, 23436},
                {256.00, "CALL涨",2.85, 3.2, 0.18, 2.89, 2.87, 5000, 23436},
                {255.50, "CALL涨", 2.85, 3.2, 0.18, 2.89, 2.87, 5000, 23436},
                {255.50, "PUT跌",2.85, 3.2, 0.18, 2.89, 2.87, 5000, 23436},
                {256.00, "CALL涨",2.85, 3.2, 0.18, 2.89, 2.87, 5000, 23436},
                {256.00, "PUT跌",2.85, 3.2, 0.18, 2.89, 2.87, 5000, 23436}};
        SOptionTableModel tableModel = new SOptionTableModel();
        // JTable table = new JTable(data,columnNames);
        JTable table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setColumWidth(table, 75, 25);
        return table;
    }




}
