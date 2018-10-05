package com.table;

import com.render.SRosePercentRender;
import com.render.SRoserender;
import com.utils.SUtil;
import com.utils.TConst;
import java.util.List;
import javax.swing.JTable;
import static com.utils.TFileUtil.getConfigValue;

/**
 * 买卖期权记录表
 */


public class SOperateStatisticTable extends JTable
{

    private static String symbol = getConfigValue("symbol", TConst.CONFIG_I18N_FILE); // 标的
    private static String currentPrice = getConfigValue("current.price", TConst.CONFIG_I18N_FILE); //最新价
    private static String averageBuyPrice = getConfigValue("average.buy.price", TConst.CONFIG_I18N_FILE); // 持仓价
    private static String count = getConfigValue("count", TConst.CONFIG_I18N_FILE); // 数量
    private static String marketValue = getConfigValue("market.value", TConst.CONFIG_I18N_FILE); // 市值
    private static String zdf = getConfigValue("zdf", TConst.CONFIG_I18N_FILE); //涨跌幅
    private static String saleprice = getConfigValue("sale.price", TConst.CONFIG_I18N_FILE); // 卖价
    private static String yinorkui = getConfigValue("yin.or.kui", TConst.CONFIG_I18N_FILE); // 盈亏/
    private static String time = getConfigValue("time", TConst.CONFIG_I18N_FILE); // 时间

    String[] columnNames = {symbol, currentPrice, averageBuyPrice, count, marketValue, zdf, saleprice, yinorkui, time};


    private TCyTableModel operateStatisticTableModel;

    public SOperateStatisticTable()
    {
        operateStatisticTableModel = new TCyTableModel(columnNames);
        setModel(operateStatisticTableModel);
        getColumnModel().getColumn(5).setCellRenderer(new SRosePercentRender());
        getColumnModel().getColumn(7).setCellRenderer(new SRoserender());
        SUtil.setTableColumWidth(this, 55);
        SUtil.setTableColumWidth(this, 0, 145);
        setRowHeight(22);
    }


    public void updateData(Object[][] tableData)
    {
        operateStatisticTableModel.updateData(tableData);
    }

    public void addRowData(Object userObj, List<Object> rowdata)
    {
        operateStatisticTableModel.addRowData(userObj, rowdata);
    }

    public void updateData(Object userObj, List<Object> rowdata)
    {
        operateStatisticTableModel.updateRowData(userObj, rowdata);
    }

    public void addRowData(Object userObj, List<Object> rowdata, int rowIndex)
    {
        operateStatisticTableModel.addRowData(userObj, rowdata, rowIndex);
    }

    public void setValueAt(int rowIndex, int colIndex, Object value)
    {
        if (operateStatisticTableModel != null)
        {
            operateStatisticTableModel.setValueAt(value, rowIndex, colIndex);
        }
    }

    public Object getRowUserObject(int rowIndex)
    {
        return operateStatisticTableModel != null ? operateStatisticTableModel.getRowUserObject(rowIndex) : null;
    }


}
