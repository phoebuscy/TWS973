package com.commdata.mbassadorObj;


import com.ib.client.Contract;

/**
 * 这个方法仅在已经调用EClientSocket对象的reqAccountUpdates()方法时调用。
 * 参数	描述
 * contract	该结构包括交易合约的描述。 合约的交易所区域没有为投资组合更新而设置。
 * position	该整数表示合约头寸。 如果头寸为0，表示头寸刚被平仓。
 * marketPrice	产品的单位价格。
 * marketValue	产品的总市值。
 * averageCost	每股的平均成本的计算是用你头寸的数量除以你的成本（执行价格＋佣金）。
 * unrealizedPNL	你未平仓头寸的当前市值和平均成本或平均成本值的差。
 * realizedPNL	显示平仓头寸的利润，为你的建仓执行成本（执行价格＋建仓佣金）和平仓执行成本（执行价格＋平仓头寸佣金）的差。
 * accountName	信息应用于的账户名称。适用于金融顾问子账户信息。
 */

public class MBAPortFolio
{
    public Contract contract;
    public double position;
    public double marketPrice;
    public double marketValue;
    public double averageCost;
    public double unrealizedPNL;
    public double realizedPNL;
    public String accountName;

    private boolean isClose = false;

    public MBAPortFolio(Contract contract,
                        double position,
                        double marketPrice,
                        double marketValue,
                        double averageCost,
                        double unrealizedPNL,
                        double realizedPNL,
                        String accountName)
    {
        this.contract = contract;
        this.position = position;
        this.marketPrice = marketPrice;
        this.marketValue = marketValue;
        this.averageCost = averageCost;
        this.unrealizedPNL = unrealizedPNL;
        this.realizedPNL = realizedPNL;
        this.accountName = accountName;

        setifClose(); // 根据position 判断是否已平仓
    }

    public boolean isClose()
    {
        return isClose;
    }

    public void setifClose()
    {
        isClose = Double.compare(position, 0D) == 0;
    }

    public void setifClose(boolean isClose)
    {
        this.isClose = isClose;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof MBAPortFolio)
        {
            MBAPortFolio other = (MBAPortFolio) obj;
            return contract.conid() == other.contract.conid();
        }
        return false;
    }
}
