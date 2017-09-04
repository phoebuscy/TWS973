package com.table;

import javax.swing.table.AbstractTableModel;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TCyTableModel extends AbstractTableModel
{

    private String[] columnNames = null;
    private Map<BigInteger, Integer> rowData2rowIndexMap = new HashMap<>(); // 数据与rowindex的对应关系map, rowIndex可变
    private Map<BigInteger, List<Object>> rowdatas = new HashMap<>();       // 存放数据list
    private BigInteger rowObjectNum = BigInteger.ZERO; // 每行数据的标识，加一行该数则增加一

    public TCyTableModel()
    {

    }

    public TCyTableModel(String[] columnNames)
    {
        this.columnNames = columnNames;
    }

    public TCyTableModel(String[] columnNames, Object[][] datas)
    {
        this.columnNames = columnNames;
        setData(datas);
    }

    public void setData(Object[][] datas)
    {
        rowdatas.clear();
        rowData2rowIndexMap.clear();
        if (columnNames != null && datas != null && datas[0].length == columnNames.length)
        {
            int dataColCount = columnNames.length;
            int newDataColCount = dataColCount + 1;
            for (int i = 0; i < datas.length; i++)
            {
                BigInteger newRowDataNum = getNewRowObjectNum();
                Object[] rowUserObjData = new Object[newDataColCount]; // 增加第一个单元格存放userObject
                System.arraycopy(datas[i], 0, rowUserObjData, 1, dataColCount);
                rowdatas.put(newRowDataNum, Arrays.asList(datas[i]));
                rowData2rowIndexMap.put(newRowDataNum, Integer.valueOf(i));
            }
        }
        fireTableDataChanged();

    }


    @Override
    public int getRowCount()
    {
        return rowData2rowIndexMap != null ? rowData2rowIndexMap.size() : 0;
    }

    @Override
    public int getColumnCount()
    {
        return columnNames != null ? columnNames.length : 0;
    }

    @Override
    public String getColumnName(int column)
    {
        return columnNames != null && columnNames.length > column ? columnNames[column] : "null";
    }

    public void updateData(Object[][] tableData)
    {
        setData(tableData);
    }



    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        List<Object> rowDataWithUserObj = getRowDataWithUserObj(rowIndex);
        if (rowDataWithUserObj != null)
        {
            return (rowDataWithUserObj.size() > columnIndex + 1) ? rowDataWithUserObj.get(columnIndex + 1) : null;
        }
        return null;
    }

    private List<Object> getRowDataWithUserObj(int rowIndex)
    {
        if (rowdatas != null && rowData2rowIndexMap != null)
        {
            for (Map.Entry<BigInteger, Integer> entry : rowData2rowIndexMap.entrySet())
            {
                if (entry.getValue() == rowIndex)
                {
                    return rowdatas.get(entry.getKey());
                }
            }
        }
        return null;
    }

    public Object getRowUserObject(int rowIndex)
    {
        List<Object> rowDataWithUserObj = getRowDataWithUserObj(rowIndex);
        if (rowDataWithUserObj != null && rowDataWithUserObj.size() > 1)
        {
            return rowDataWithUserObj.get(0);
        }
        return null;
    }

    public void setRowUserObject(Object userObject, int rowIndex)
    {
        List<Object> rowDataWithUserObj = getRowDataWithUserObj(rowIndex);
        if (rowDataWithUserObj != null && rowDataWithUserObj.size() > 1)
        {
            rowDataWithUserObj.set(0, userObject);
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex)
    {
        List<Object> rowDataWithUserObj = getRowDataWithUserObj(rowIndex);
        if (rowDataWithUserObj != null && rowDataWithUserObj.size() > columnIndex + 1)
        {
            rowDataWithUserObj.set(columnIndex + 1, aValue);
            fireTableCellUpdated(rowIndex, columnIndex);
        }
    }


    public void addRowData(List<Object> rowdata)
    {
        addRowData(null, rowdata, Integer.MAX_VALUE);
    }

    public void addRowData(Object userObj, List<Object> rowdata, int rowIndex)
    {
        if (rowdata != null && rowdata.size() == columnNames.length)
        {
            int dataColCount = columnNames.length;
            int newDataColCount = dataColCount + 1;
            BigInteger newRowDataNum = getNewRowObjectNum();

            List<Object> rowDataWithUserObj = new ArrayList<>(newDataColCount);
            rowDataWithUserObj.add(userObj);  // 增加第一个单元格存放userObject
            rowDataWithUserObj.addAll(rowdata);
            rowdatas.put(newRowDataNum, rowDataWithUserObj);
            rowIndex = (rowIndex >= rowData2rowIndexMap.size()) ? rowData2rowIndexMap.size() : rowIndex;
            // 更改rowData2rowIndexMap中 value值比rowIndex 大的值（加1）
            increOrDecreRowIndex(true, rowIndex);
            rowData2rowIndexMap.put(newRowDataNum, rowIndex);
            fireTableRowsInserted(rowIndex, rowIndex);
        }
    }

    // 增加或减少1
    private void increOrDecreRowIndex(boolean isIncreOne, Integer beginRowIndex)
    {
        if (rowData2rowIndexMap != null)
        {
            int inOrCre = isIncreOne ? 1 : -1;
            for (Map.Entry<BigInteger, Integer> entry : rowData2rowIndexMap.entrySet())
            {
                Integer val = entry.getValue();
                if (val >= beginRowIndex)
                {
                    entry.setValue(val + inOrCre);
                }
            }
        }
    }

    public void addRowData(int rowIndex, List<Object> rowdata)
    {
        addRowData(null, rowdata, rowIndex);
    }

    private BigInteger getNewRowObjectNum()
    {
        rowObjectNum = rowObjectNum.add(BigInteger.ONE);
        return rowObjectNum;
    }

    public void deleteRow(int[] rowIndexs)
    {
        if (rowData2rowIndexMap != null && rowdatas != null && rowIndexs != null)
        {
            Set<Integer> validRowIndex = new HashSet<>();
            int rowSize = rowData2rowIndexMap.size();
            for (int i : rowIndexs)
            {
                if (i < rowSize)
                {
                    validRowIndex.add(i);
                }
            }

            // 获取到 rowdatas 中的Key
            List<BigInteger> rowObjLst = new ArrayList<>();
            for (Map.Entry<BigInteger, Integer> entry : rowData2rowIndexMap.entrySet())
            {
                if (validRowIndex.contains(entry.getValue()))
                {
                    rowObjLst.add(entry.getKey());
                }
            }

            List<Integer> validRowIdLst = new ArrayList<>(validRowIndex);
            Collections.reverse(validRowIdLst);
            for (Integer rowIndex : validRowIdLst)
            {
                increOrDecreRowIndex(false, rowIndex);
            }

            for (BigInteger rowObj : rowObjLst)
            {
                rowData2rowIndexMap.remove(rowObj);
                rowdatas.remove(rowObj);
            }

            for (Integer rowIndex : validRowIdLst)
            {
                fireTableRowsDeleted(rowIndex, rowIndex);
            }
        }

    }


}
