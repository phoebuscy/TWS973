package com.table.tableModel;

import com.TConst;

import javax.swing.table.AbstractTableModel;

import static com.TFileUtil.getConfigValue;

/**
 * Created by 123 on 2016/12/24.
 */
public class SOptionLinkTableModel extends AbstractTableModel
{

    private static String xqj = getConfigValue("xqj", TConst.CONFIG_I18N_FILE); // 行权价
    private static String callOrput = getConfigValue("call.put", TConst.CONFIG_I18N_FILE); //'CALL/PUT'
    private static String currentPrice = getConfigValue("current.price", TConst.CONFIG_I18N_FILE); //最新价
    private static String zde = getConfigValue("zde", TConst.CONFIG_I18N_FILE); //涨跌额'
    private static String zdf = getConfigValue("zdf", TConst.CONFIG_I18N_FILE); //涨跌幅
    private static String sellOne = getConfigValue("sell.one", TConst.CONFIG_I18N_FILE); //卖一
    private static String buyOne = getConfigValue("buy.one", TConst.CONFIG_I18N_FILE); //买一
    private static String cjl = getConfigValue("cjl", TConst.CONFIG_I18N_FILE);  //成交量
    private static String wpc = getConfigValue("wpc", TConst.CONFIG_I18N_FILE); //未平仓

    private static String callRaise = getConfigValue("call.raise", TConst.CONFIG_I18N_FILE); //CALL涨
    private static String putDown = getConfigValue("put.down", TConst.CONFIG_I18N_FILE); //PUT跌

    private String[] columnNames = {xqj, callOrput,currentPrice, zde, zdf, sellOne, buyOne, cjl, wpc};
    private Object[][] data = {
            {255.50, callRaise, 2.85, 3.2, 0.18, 2.89, 2.87, 5000, 23436},
            {255.50, putDown,2.85, 3.2, 0.18, 2.89, 2.87, 5000, 23436},
            {256.00, callRaise,2.85, 3.2, 0.18, 2.89, 2.87, 5000, 23436},
            {256.00, putDown,2.85, 3.2, 0.18, 2.89, 2.87, 5000, 23436}};



    public SOptionLinkTableModel()
    {


    }

    @Override
    public int getRowCount()
    {
        return data.length;
    }

    @Override
    public int getColumnCount()
    {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column)
    {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column)
    {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        return data[rowIndex][columnIndex];
    }

    public void updateData(Object[][] tableData)
    {
        data = tableData;
    }



}
