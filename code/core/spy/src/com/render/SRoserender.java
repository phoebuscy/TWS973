package com.render;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.math.BigInteger;

import static com.SUtil.getShowNumberLabel;

/**
 * Created by caiyong on 2017/1/17.
 */
public class SRoserender implements TableCellRenderer
{


    @Override
    public Component getTableCellRendererComponent(JTable table,
                                                   Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row,
                                                   int column)
    {

        Color background = isSelected ? table.getSelectionBackground() : table.getBackground();
        JLabel label = getShowNumberLabel(value, false, BigInteger.ONE, null, Boolean.TRUE);
        label.setBackground(background);
        return label;
    }

}
