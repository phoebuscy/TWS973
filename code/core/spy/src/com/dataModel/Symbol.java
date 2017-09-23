package com.dataModel;

import com.ib.client.ContractDetails;
import java.util.ArrayList;
import java.util.List;

/**
 * ������
 */



public class Symbol
{

    private String symbolVal = ""; // ��������
    private SDataManager dataManager;

    private List<ContractDetails> contractDetailsList = new ArrayList<>();

    public Symbol(SDataManager dataManager)
    {
        this.dataManager = dataManager;
    }

    public Symbol(String symbolVal)
    {
        this.symbolVal = symbolVal;
    }


    public String getSymbolVal()
    {
        return symbolVal;
    }

    public void setSymbolVal(String symbleVal)
    {
        this.symbolVal = symbleVal;
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
