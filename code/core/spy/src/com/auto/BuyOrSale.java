package com.auto;

import com.ib.client.Contract;

/**
 * ���ฺ�����Cculateģ�鷢�͵� ������ģ����������������
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
     * �����
     * @param contract ���
     */
    public void buy(Contract contract)
    {

    }

    /**
     * ������
     * @param contract ���
     */
    public void sale(Contract contract)
    {

    }


}
