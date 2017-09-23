package com.dataModel.mbassadorObj;

import com.ib.client.ContractDetails;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OptionChainMap
{

    private Map<Double, List<ContractDetails>> strike2ContractDtalsLst = new HashMap<>();


    public OptionChainMap(Map<Double, List<ContractDetails>> optionChainMap)
    {
        strike2ContractDtalsLst = optionChainMap != null? optionChainMap: new HashMap<>();
    }

    public Map<Double, List<ContractDetails>> getStrike2ContractDtalsLst()
    {
        return strike2ContractDtalsLst;
    }


    public void setStrike2ContractDtalsLst(Map<Double, List<ContractDetails>> optionChainMap)
    {
        if(optionChainMap != null)
        {
            strike2ContractDtalsLst = optionChainMap;
        }
    }
}
