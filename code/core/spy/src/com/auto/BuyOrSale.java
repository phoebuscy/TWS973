package com.auto;

import com.ib.client.Contract;

/**
 * 该类负责接收Cculate模块发送的 买卖标的，及买卖点进行买卖
 */

public class BuyOrSale
{

    private static BuyOrSale instance = new BuyOrSale();

    private BuyOrSale()
    {

    }

    public static BuyOrSale getInstance()
    {
        return instance;
    }


    /**
     * 买操作
     * @param contract 标的
     */
    public void buy(Contract contract)
    {

    }

    /**
     * 卖操作
     * @param contract 标的
     */
    public void sale(Contract contract)
    {

    }


}
