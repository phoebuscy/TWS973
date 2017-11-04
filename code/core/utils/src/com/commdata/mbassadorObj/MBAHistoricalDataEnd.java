package com.commdata.mbassadorObj;

public class MBAHistoricalDataEnd
{
    public int reqId;
    public String startDateStr;
    public String endDateStr;

    public MBAHistoricalDataEnd(int reqId, String startDateStr, String endDateStr)
    {
        this.reqId = reqId;
        this.startDateStr = startDateStr;
        this.endDateStr = endDateStr;
    }



}
