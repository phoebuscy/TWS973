package com.dataModel;

import com.ib.client.ContractDetails;
import java.util.ArrayList;
import java.util.List;

/**
 * ������
 */



public class Symbol
{

    private String symbleVal = ""; // ��������

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
