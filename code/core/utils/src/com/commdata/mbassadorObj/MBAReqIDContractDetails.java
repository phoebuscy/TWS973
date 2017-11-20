package com.commdata.mbassadorObj;

import com.ib.client.ContractDetails;

public class MBAReqIDContractDetails
{
    public long reqid;
    public ContractDetails contractDetails;
    public double currentPrice = 0D;
    public double yesterdayClose = 0D;
    public double todayOpen = 0D;

    public MBAReqIDContractDetails(long reqid,
                                   ContractDetails contractDetails,
                                   double currentPrice,
                                   double yesterdayClose,
                                   double todayOpen)
    {
        this.reqid = reqid;
        this.currentPrice = currentPrice;
        this.contractDetails = contractDetails;
        this.yesterdayClose = yesterdayClose;
        this.todayOpen = todayOpen;
    }
}
