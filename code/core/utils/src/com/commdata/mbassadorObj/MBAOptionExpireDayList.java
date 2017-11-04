package com.commdata.mbassadorObj;

import java.util.ArrayList;
import java.util.List;

public class MBAOptionExpireDayList
{
    public List<String> optionExpireDayList = new ArrayList<>();
    public MBAOptionExpireDayList(List<String> optionExpireDayList)
    {
        this.optionExpireDayList.clear();
        if(optionExpireDayList != null)
        {
            this.optionExpireDayList.addAll(optionExpireDayList);
        }
    }

}
