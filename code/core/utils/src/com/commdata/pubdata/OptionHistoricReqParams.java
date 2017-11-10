package com.commdata.pubdata;

import com.ib.client.Contract;
import com.ib.client.Types;

/**
 * ��ѯ��Ȩ��ʷ���ݵ���������࣬��������������
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
