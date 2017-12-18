package com.commdata.mbassadorObj;

import com.ib.client.Contract;

public class MBABeginQuerySymbol
{
    public Contract contract;

    public MBABeginQuerySymbol(Contract contract)
    {
        this.contract = contract;
    }

    public Contract getContract()
    {
        return contract;
    }


}
