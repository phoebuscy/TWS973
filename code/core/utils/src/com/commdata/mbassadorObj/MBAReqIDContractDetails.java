package com.commdata.mbassadorObj;

import com.ib.client.Contract;
import com.ib.client.ContractDetails;

public class MBAReqIDContractDetails
{
    public long reqid;
    public Contract contract;
    public ContractDetails contractDetails;
    public double currentPrice = 0D;
    public double yesterdayClose = 0D;
    public double todayOpen = 0D;

    public MBAReqIDContractDetails(long reqid,
                                   Contract contract,
                                   ContractDetails contractDetails,
                                   double currentPrice,
                                   double yesterdayClose,
                                   double todayOpen)
    {
        this.reqid = reqid;
        this.contract = contract;
        this.currentPrice = currentPrice;
        this.contractDetails = contractDetails;
        this.yesterdayClose = yesterdayClose;
        this.todayOpen = todayOpen;
    }
}
