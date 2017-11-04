package com.commdata.mbassadorObj;

public class MBAHistoricalData
{
    public int reqId;
    public String date;
    public double open;
    public double high;
    public double low;
    public double close;
    public int volume;
    public int count;
    public double WAP;
    public boolean hasGaps;

    public MBAHistoricalData(int reqId,
                             String date,
                             double open,
                             double high,
                             double low,
                             double close,
                             int volume,
                             int count,
                             double WAP,
                             boolean hasGaps)
    {
        this.reqId = reqId;
        this.date = date;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
        this.count = count;
        this.WAP = WAP;
        this.hasGaps = hasGaps;

    }

    public MBAHistoricalData(int reqId,
                             String date,
                             double open,
                             double high,
                             double low,
                             double close
                           )
    {
        this.reqId = reqId;
        this.date = date;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = 0;
        this.count = 0;
        this.WAP = 0;
        this.hasGaps = false;
    }

}
