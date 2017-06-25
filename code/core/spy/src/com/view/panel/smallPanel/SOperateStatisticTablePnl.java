package com.view.panel.smallPanel;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;

import static com.utils.SUtil.getDimension;
import static com.utils.SUtil.setColumWidth;

/**
 * Created by caiyong on 2016/12/25.
 */
public class SOperateStatisticTablePnl extends JPanel
{
    private Component parentWin;
    private Dimension parentDimension;

    private JTable table;
    private TableModel tableModel;

    public SOperateStatisticTablePnl(Component parentWin)
    {
        setBackground(Color.blue);
        this.parentWin = parentWin;
        parentDimension = parentWin.getSize();
        setDimension();
        buildGUI();
    }

    private void setDimension()
    {
       setSize(getDimension(parentDimension,0.5,0.8));
    }

    private void buildGUI()
    {
        setLayout(new BorderLayout());
        table = crtTable();
        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);
        add(scrollPane);
        setPreferredSize(getDimension(parentDimension,0.6,0.5));
    }

    private JTable crtTable()
    {
        String[] columnNames = {"标的","买价", "卖价", "数量","盈亏", "时间"};
        Object[][] data = {
                {"SPY 20161116 215.5 PUT", 1.50, 2.50, 10, 100, "201610122330"},
                {"SPY 20161116 215.5 PUT", 1.50, 2.50, 10, 100, "201610122330"},
                {"SPY 20161116 215.5 PUT", 1.50, 2.50, 10, 100, "201610122330"},
                {"SPY 20161116 215.5 PUT", 1.50, 2.50, 10, 100, "201610122330"},
                {"SPY 20161116 215.5 PUT", 1.50, 2.50, 10, 100, "201610122330"},
                {"SPY 20161116 215.5 PUT", 1.50, 2.50, 10, 100, "201610122330"},
                {"SPY 20161116 215.5 PUT", 1.50, 2.50, 10, 100, "201610122330"}};
        JTable table = new JTable(data,columnNames);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        setColumWidth(table, 75, 25);
        return table;
    }



}
