package com.dataModel;

import com.ib.client.ContractDetails;
import java.util.ArrayList;
import java.util.List;

/**
 * 对象类
 */



public class Symbol
{

    private String symbleVal = ""; // 对象名称

    private List<ContractDetails> contractDetailsList = new ArrayList<>();


    public Symbol(String symbleVal)
    {
        this.symbleVal = symbleVal;
    }


    public String getSymbleVal()
    {
        return symbleVal;
    }

    public void setSymbleVal(String symbleVal)
    {
        this.symbleVal = symbleVal;
    }

    public void addContractdetails(ContractDetails contractDetails)
    {
        if(contractDetails != null )
        {
            contractDetailsList.add(contractDetails);
        }
    }

    public List getContractdetails()
    {
        return contractDetailsList;
    }


}
