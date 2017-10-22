package com.view.panel.smallPanel;

import com.utils.TConst;
import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;

import static com.utils.SUtil.getDimension;
import static com.utils.SUtil.setColumWidth;
import static com.utils.TFileUtil.getConfigValue;

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
        String symbol = getConfigValue("symbol", TConst.CONFIG_I18N_FILE); // 标的
        String buyprice = getConfigValue("buy.price", TConst.CONFIG_I18N_FILE); // 买价
        String saleprice  = getConfigValue("sale.price", TConst.CONFIG_I18N_FILE); // 卖家
        String count = getConfigValue("count", TConst.CONFIG_I18N_FILE); // 数量
        String yinorkui = getConfigValue("yin.or.kui", TConst.CONFIG_I18N_FILE); // 盈亏/
        String time = getConfigValue("time", TConst.CONFIG_I18N_FILE); // 时间

        String[] columnNames = {symbol,buyprice,saleprice,count,yinorkui, time};
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
