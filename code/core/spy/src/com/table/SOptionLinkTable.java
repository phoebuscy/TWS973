package com.table;

import com.render.SRosePercentRender;
import com.render.SRoserender;
import com.utils.TConst;

import java.util.List;
import javax.swing.JTable;
import java.util.Random;

import static com.utils.SUtil.setColumWidth;
import static com.utils.TFileUtil.getConfigValue;

/**
 * Created by caiyong on 2017/1/15.
 */
public class SOptionLinkTable extends JTable
{

    private static String callOrput = getConfigValue("call.put", TConst.CONFIG_I18N_FILE); //'CALL/PUT'
    private static String strike = getConfigValue("strike", TConst.CONFIG_I18N_FILE); // 行权价
    private static String currentPrice = getConfigValue("current.price", TConst.CONFIG_I18N_FILE); //最新价
    private static String zde = getConfigValue("zde", TConst.CONFIG_I18N_FILE); //涨跌额'
    private static String zdf = getConfigValue("zdf", TConst.CONFIG_I18N_FILE); //涨跌幅
    private static String sellOne = getConfigValue("sell.one", TConst.CONFIG_I18N_FILE); //卖一
    private static String buyOne = getConfigValue("buy.one", TConst.CONFIG_I18N_FILE); //买一
    private static String cjl = getConfigValue("cjl", TConst.CONFIG_I18N_FILE);  //成交量
    private static String wpc = getConfigValue("wpc", TConst.CONFIG_I18N_FILE); //未平仓

    private static String callRaise = getConfigValue("call.raise", TConst.CONFIG_I18N_FILE); //CALL涨
    private static String putDown = getConfigValue("put.down", TConst.CONFIG_I18N_FILE); //PUT跌

    private String[] columnNames = {callOrput, strike, currentPrice, zde, zdf, sellOne, buyOne, cjl, wpc};

    private Object[][] data = {{258.50, callRaise, 2.85, 3.2, 0.18, 2.89, 2.87, 5000, 23436},
                               {245.50, putDown, 2.85, 4.2, 0.18, 2.89, 2.87, 5000, 23436},
                               {256.00, callRaise, 2.85, 3.2, 0.18, 2.89, 2.87, 5000, 23436},
                               {256.00, putDown, 2.85, 3.2, 0.18, 2.89, 2.87, 5000, 23436}};


    private TCyTableModel optionLinkTabelModel;

    public SOptionLinkTable()
    {
        optionLinkTabelModel = new TCyTableModel(columnNames, data);
        setModel(optionLinkTabelModel);
        getColumnModel().getColumn(3).setCellRenderer(new SRoserender());
        getColumnModel().getColumn(4).setCellRenderer(new SRosePercentRender());
        setColumWidth(this, 80, 22);
    }


    public void updateData(Object[][] tableData)
    {
        optionLinkTabelModel.updateData(tableData);
    }

    public void addRowData(Object userObj, List<Object> rowdata)
    {
        optionLinkTabelModel.addRowData(userObj, rowdata);
    }

    public void addRowData(Object userObj, List<Object> rowdata, int rowIndex)
    {
        optionLinkTabelModel.addRowData(userObj, rowdata, rowIndex);
    }

    public void setValueAt(int rowIndex, int colIndex, Object value)
    {
        if (optionLinkTabelModel != null)
        {
            optionLinkTabelModel.setValueAt(value, rowIndex, colIndex);
        }
    }


    private Object[][] getTableData()
    {
        String callRaise = getConfigValue("call.raise", TConst.CONFIG_I18N_FILE); //CALL涨
        String putDown = getConfigValue("put.down", TConst.CONFIG_I18N_FILE); //PUT跌

        Object[][] data1 = {{2552.50, callRaise, 2.85, 3.2, 0.18, 2.89, 2.87, 5000, 23436},
                            {255.50, putDown, 2.85, -3.2, -0.18, 2.89, 2.87, 5000, 23436},
                            {2561.00, callRaise, 2.85, 3.2, 0.18, 2.89, 2.87, 5000, 23436},
                            {255.50, putDown, 2.85, 3.2, 0.18, 2.89, 2.87, 5000, 23436},
                            {2561.00, callRaise, 2.85, -1.2, -0.18, 2.89, 2.87, 5000, 23436},
                            {2561.00, callRaise, 2.85, 3.2, 0.18, 2.89, 2.87, 5000, 23436},
                            {255.50, putDown, 2.85, -3.2, 0.18, 2.89, 2.87, 5000, 23436},
                            {256.00, putDown, 2.85, 3.2, 0.18, 2.89, 2.87, 5000, 23436}};

        Object[][] data2 = {{2553.50, callRaise, 2.85, 3.2, 0.18, 2.89, 2.87, 5000, 23436},
                            {2561.00, callRaise, 2.85, +3.2, +0.18, 2.89, 2.87, 5000, 23436},
                            {255.50, putDown, 2.85, 3.2, 0.18, 2.89, 2.87, 5000, 23436},
                            {2565.02, putDown, 2.85, -3.2, -0.18, 2.89, 2.87, 5000, 23436}};

        Random random = new Random();
        boolean b = random.nextBoolean();

        return b ? data1 : data2;
    }


}
