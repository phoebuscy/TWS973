package com.commdata.pubdata;

import com.ib.client.Contract;
import com.ib.client.TickType;

public class ContractRealTimeInfo implements Cloneable
{
    public Contract contract;
    public double lastPrice = 0D;
    public double yesterdayClose = 0D;
    public double todayOpen = 0d;
    public double maxHigh = 0d;
    public double minLow = 0D;
    public double buyPrice = 0D;
    public double salePrice = 0D;

    public TickType tickType = TickType.UNKNOWN;


    public ContractRealTimeInfo(Contract contract)
    {
        this.contract = contract;
    }

    public ContractRealTimeInfo(ContractRealTimeInfo other, TickType tickType)
    {
        if (other != null)
        {
            contract = other.contract.clone();

            lastPrice = other.lastPrice;
            yesterdayClose = other.yesterdayClose;
            todayOpen = other.todayOpen;
            maxHigh = other.maxHigh;
            minLow = other.minLow;
            buyPrice = other.buyPrice;
            salePrice = other.salePrice;
            this.tickType = (tickType == null) ? other.tickType : tickType;
        }
    }


    public void setPrice(TickType tickType, double price)
    {
        if (tickType != null)
        {
            switch (tickType)
            {
                case ASK:
                    salePrice = price;
                    break;
                case BID:
                    buyPrice = price;
                    break;
                case LAST:
                    lastPrice = price;
                    break;
                case LOW:
                    minLow = price;
                    break;
                case HIGH:
                    maxHigh = price;
                    break;
                case OPEN:
                    todayOpen = price;
                    break;
                case CLOSE:
                    yesterdayClose = price;
                    break;
            }
        }

    }

    @Override
    public ContractRealTimeInfo clone()
    {
        try
        {
            ContractRealTimeInfo copy = (ContractRealTimeInfo) super.clone();
            return copy;
        }
        catch (CloneNotSupportedException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        return (obj instanceof Contract) && ((Contract) obj).conid() == contract.conid();
    }


    public static void main(String[] args)
    {
        Contract srcC = new Contract();
        srcC.conid(110);

        ContractRealTimeInfo ctr1 = new ContractRealTimeInfo(srcC);

        ContractRealTimeInfo ctr2 = ctr1.clone();


        int a = 1;
    }


}
