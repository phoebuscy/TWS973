package com.model;

import com.ib.client.Types;

/**
 * Created by caiyong on 2016/12/26.
 */
public class SOptionRealTimeInfoModel
{
    private Types.Right right;  // put or call

    private String obj; // 标的
    private String expireDate;
    private String operatePrice; // 行权价
    private String realTimePrice; // 当前实时价格
    private String todayOpenPrice; // 今开价
    private String yestadayClosePrice; // 昨天收价
    private String todayMaxPrice;  // 最高价
    private String todayMinPrice;  // 最低价
    private String notCloseCount;   // 未平仓数量
    private String curSellPrice;   // 当前实时卖价
    private String curSellCount;  // 当前实时卖量
    private String curBuyPrice;   // 当前实时买价
    private String curBuyCount;    // 当前实时买量
    private String tradingVol; // 成交量


    public SOptionRealTimeInfoModel()
    {
        this.right = Types.Right.None;
        obj = ""; // 标的
        expireDate = "";
        operatePrice = "";
        realTimePrice = "";
        todayOpenPrice = "";
        yestadayClosePrice = "";
        todayMaxPrice = "";
        todayMinPrice = "";
        notCloseCount = "";

        curSellPrice = "";
        curSellCount = "";
        curBuyPrice = "";
        curBuyCount = "";
        tradingVol = "";
    }

    public void setRealTimeData(Types.Right right,
                                String obj,
                                String expireDate,
                                String operatePrice,
                                String realTimePrice,
                                String todayOpenPrice,
                                String yestadayClosePrice,
                                String todayMaxPrice,
                                String todayMinPrice,
                                String notCloseCount,
                                String curSellPrice,
                                String curSellCount,
                                String curBuyPrice,
                                String curBuyCount,
                                String tradingVol)
    {
        this.right = right;
        this.obj = obj; // 标的
        this.expireDate = expireDate;
        this.operatePrice = operatePrice;
        this.realTimePrice = realTimePrice;
        this.todayOpenPrice = todayOpenPrice;
        this.yestadayClosePrice = yestadayClosePrice;
        this.todayMaxPrice = todayMaxPrice;
        this.todayMinPrice = todayMinPrice;
        this.notCloseCount = notCloseCount;

        this.curSellPrice = curSellPrice;
        this.curSellCount = curSellCount;
        this.curBuyPrice = curBuyPrice;
        this.curBuyCount = curBuyCount;
        this.tradingVol = tradingVol;
    }

    public void setRight(Types.Right right)
    {
        this.right = right;
    }

    public void setObj(String obj)
    {
        this.obj = obj;
    }

    public void setExpireDate(String expireDate)
    {
        this.expireDate = expireDate;
    }

    public void setOperatePrice(String operatePrice)
    {
        this.operatePrice = operatePrice;
    }

    public void setRealTimePrice(String realTimePrice)
    {
        this.realTimePrice = realTimePrice;
    }

    public void setTodayOpenPrice(String todayOpenPrice)
    {
        this.todayOpenPrice = todayOpenPrice;
    }

    public void setYestadayClosePrice(String yestadayClosePrice)
    {
        this.yestadayClosePrice = yestadayClosePrice;
    }

    public void setTodayMaxPrice(String todayMaxPrice)
    {
        this.todayMaxPrice = todayMaxPrice;
    }

    public void setTodayMinPrice(String todayMinPrice)
    {
        this.todayMinPrice = todayMinPrice;
    }

    public void setNotCloseCount(String notCloseCount)
    {
        this.notCloseCount = notCloseCount;
    }

    public void setCurSellPrice(String curSellPrice)
    {
        this.curSellPrice = curSellPrice;
    }

    public void setCurSellCount(String curSellCount)
    {
        this.curSellCount = curSellCount;
    }

    public void setCurBuyPrice(String curBuyPrice)
    {
        this.curBuyPrice = curBuyPrice;
    }

    public void setCurBuyCount(String curBuyCount)
    {
        this.curBuyCount = curBuyCount;
    }

    public void setTradingVol(String tradingVol)
    {
        this.tradingVol = tradingVol;
    }

    public Types.Right getRight()
    {
        return right;
    }

    public String getObj()
    {
        return obj;
    }

    public String getExpireDate()
    {
        return expireDate;
    }
    public String getOperatePrice()
    {
        return operatePrice;
    }

    public String getRealTimePrice()
    {
        return realTimePrice;
    }
    public String getTodayOpenPrice()
    {
        return todayOpenPrice;
    }
    public String getYestadayClosePrice()
    {
        return yestadayClosePrice;
    }
    public String getTodayMaxPrice()
    {
        return todayMaxPrice;
    }
    public String getTodayMinPrice()
    {
        return todayMinPrice;
    }
    public String getNotCloseCount()
    {
        return notCloseCount;
    }

    public String getCurSellPrice()
    {
        return curSellPrice;
    }

    public String getCurSellCount()
    {
        return curSellCount;
    }

    public String getCurBuyPrice()
    {
        return curBuyPrice;
    }

    public String getCurBuyCount()
    {
        return curBuyCount;
    }

    public String getTradingVol()
    {
        return tradingVol;
    }


}
