package com.table;

import com.TConst;
import com.render.SRosePercentRender;
import com.render.SRoserender;
import com.table.tableModel.SOptionLinkTableModel;

import javax.swing.*;
import java.util.Random;

import static com.TFileUtil.getConfigValue;
import static com.SUtil.setColumWidth;

/**
 * Created by caiyong on 2017/1/15.
 */
public class SOptionLinkTable extends JTable
{

    private SOptionLinkTableModel optionLinkTabelModel;

    public SOptionLinkTable()
    {
        optionLinkTabelModel = new SOptionLinkTableModel();
        setModel(optionLinkTabelModel);
        getColumnModel().getColumn(3).setCellRenderer(new SRoserender());
        getColumnModel().getColumn(4).setCellRenderer(new SRosePercentRender());
        setColumWidth(this, 80, 22);
    }


    public void updateData(Object[][] tableData)
    {
        optionLinkTabelModel.updateData(getTableData());
        optionLinkTabelModel.fireTableDataChanged();
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
