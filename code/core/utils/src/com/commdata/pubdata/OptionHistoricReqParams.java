package com.commdata.pubdata;

import com.ib.client.Contract;
import com.ib.client.Types;

/**
 * 查询期权历史数据的请求参数类，保存的是请求参数
 */

public class OptionHistoricReqParams
{
    public Contract contract;
    public String endDateTime;
    public long duration;
    public Types.DurationUnit durationUnit;
    public Types.BarSize barSize;

    public OptionHistoricReqParams(Contract contract,
                                   String endDateTime,
                                   long duration,
                                   Types.DurationUnit durationUnit,
                                   Types.BarSize barSize)
    {
        this.contract = contract;
        this.endDateTime = endDateTime;
        this.duration = duration;
        this.durationUnit = durationUnit;
        this.barSize = barSize;
    }

}
