package com.commdata.mbassadorObj;

import com.ib.client.ContractDetails;

public class MBAReqIDContractDetails
{
    public long reqid;
    public ContractDetails contractDetails;

    public MBAReqIDContractDetails(long reqid, ContractDetails contractDetails)
    {
        this.reqid = reqid;
        this.contractDetails = contractDetails;
    }
}
