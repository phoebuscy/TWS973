package com.dataModel.mbassadorObj;


import com.ib.client.TickAttr;

/**
 * ���ڽ���symbolʵʱ�۸������
 */

public class MBAtickPrice
{
    public int tickerId;
    public int field;
    public double price;
    public TickAttr attrib;


    public MBAtickPrice(int tickerId, int field, double price, TickAttr attrib)
    {
        this.tickerId = tickerId;
        this.field = field;
        this.price = price;
        this.attrib = attrib;
    }

}
