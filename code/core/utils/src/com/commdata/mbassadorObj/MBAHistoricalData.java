package com.commdata.mbassadorObj;

import java.time.LocalDateTime;

import static com.utils.SUtil.getLocalDateTimeByEpochSecond;

public class MBAHistoricalData implements Cloneable
{
    public int reqId;
    public String date;
    public double open;
    public double high;
    public double low;
    public double close;
    public long volume;
    public int count;
    public double WAP;
    public boolean hasGaps;

    public MBAHistoricalData(int reqId,
                             String date,
                             double open,
                             double high,
                             double low,
                             double close,
                             long volume,
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

    @Override
    public String toString()
    {
        LocalDateTime dateTime = getLocalDateTimeByEpochSecond(date);
        return "reqId: " +  reqId + ", Time: " + dateTime.toString() + ", open: " + open + ", high: " + high + ", low: " + low + ", " +
                "close: " + close;
    }


    @Override
    public MBAHistoricalData clone()
    {
        try
        {
            MBAHistoricalData copy = (MBAHistoricalData) super.clone();
            return copy;
        }
        catch (CloneNotSupportedException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
