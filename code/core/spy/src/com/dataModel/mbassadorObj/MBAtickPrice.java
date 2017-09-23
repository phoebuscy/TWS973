package com.dataModel.mbassadorObj;


import com.ib.client.TickAttr;

/**
 * 用于接收symbol实时价格的类型
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
