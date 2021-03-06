package com.view.panel;

import com.commdata.mbassadorObj.MBABeginQuerySymbol;
import com.commdata.mbassadorObj.MBAHistoricalData;
import com.commdata.pubdata.ContractRealTimeInfo;
import com.commdata.pubdata.ProcessInAWT;
import com.dataModel.SDataManager;
import com.dataModel.Symbol;
import com.ib.client.Contract;
import com.ib.client.TickType;
import com.ib.client.Types;
import com.utils.GBC;
import com.utils.SUtil;
import com.utils.TMbassadorSingleton;
import com.view.panel.smallPanel.SRealTimePnl;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javafx.util.Pair;
import javax.swing.JPanel;
import net.engio.mbassy.listener.Filter;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.IMessageFilter;
import net.engio.mbassy.subscription.SubscriptionContext;
import org.jfree.data.Range;
import static com.utils.SUtil.changeToDate;
import static com.utils.SUtil.getBarSizebyDurationSeconds;
import static com.utils.SUtil.getCurrentAmericaLocalDateTime;
import static com.utils.SUtil.getCurrentDayUSACloseDateTime;
import static com.utils.SUtil.getCurrentDayUSAOpenDateTime;
import static com.utils.SUtil.getLastDayUSAOpenDateTime;
import static com.utils.SUtil.getLastOpenTimeSeconds;
import static com.utils.SUtil.getLowHighPair;
import static com.utils.SUtil.getOpenCloseDate;
import static com.utils.SUtil.getUSADateTimeByEpochSecond;
import static com.utils.SUtil.getUSAOpenDateTimeByLastDay;
import static com.utils.SUtil.ifNowIsOpenTime;
import static com.utils.SUtil.usaChangeToLocalDateTime;
import static com.utils.TConst.DATAMAAGER_BUS;
import static com.utils.TConst.REALTIMEPRICEMGR_BUS;
import static com.utils.TConst.SYMBOL_BUS;

/**
 * Created by 123 on 2016/12/18.
 */
public class SRealTimePicturePnl extends JPanel
{
    private Double price_low = 0D;
    private Double price_high = 0D;
    private LocalDateTime openUsaDateTime = null;
    private LocalDateTime closeUsaDateTime = null;
    private boolean hasDarwHistory = false;
    private Types.BarSize barSize = Types.BarSize._10_secs;
    private List<MBAHistoricalData> historicalDataList = new ArrayList<>();
    private Symbol symbol = SDataManager.getInstance().getSymbol();
    private SRealTimePnl sSpyRealTimePnl = new SRealTimePnl("spy", new Dimension(100, 200));
    private SRealTimePnl sCallRealTimePnl = new SRealTimePnl("Call", new Dimension(100, 150));
    private SRealTimePnl sPutRealTimePnl = new SRealTimePnl("Put", new Dimension(100, 150));

    private static Contract oldContract;
    private static Contract curContract;

    private Dimension parentDimension;

    public SRealTimePicturePnl(Component parentWin)
    {
        setLayout(new GridBagLayout());
        add(sSpyRealTimePnl, new GBC(0, 0).setWeight(50, 50).setFill(GBC.BOTH));
        add(sCallRealTimePnl, new GBC(0, 1).setWeight(50, 10).setFill(GBC.BOTH));
        add(sPutRealTimePnl, new GBC(0, 2).setWeight(50, 10).setFill(GBC.BOTH));

        parentDimension = parentWin.getSize();
        setDimension();

        // 订阅消息总线名称为 DATAMAAGER_BUS 的 消息
        TMbassadorSingleton.getInstance(SYMBOL_BUS).subscribe(this);
        TMbassadorSingleton.getInstance(DATAMAAGER_BUS).subscribe(this);
        TMbassadorSingleton.getInstance(REALTIMEPRICEMGR_BUS).subscribe(this);
    }

    private void setDimension()
    {
        setSize(SUtil.getDimension(parentDimension, 0.5, 1.0));
    }

    private void setXRange(LocalDateTime openDateTime, LocalDateTime closeDateTime)
    {
        if (openDateTime != null && closeDateTime != null && sSpyRealTimePnl != null)
        {
            Pair<Date, Date> openClosePair = getOpenCloseDate(openDateTime, closeDateTime);
            sSpyRealTimePnl.setXRange(openClosePair.getKey(), openClosePair.getValue());
        }
    }

    // 接收开始查询symbol的消息
    static public class beginQuerySymbolFilter implements IMessageFilter<MBABeginQuerySymbol>
    {
        @Override
        public boolean accepts(MBABeginQuerySymbol msg, SubscriptionContext subscriptionContext)
        {
            return msg != null;
        }
    }

    // 处理始查询symbol消息
    @Handler(filters = {@Filter(beginQuerySymbolFilter.class)})
    private void processBeginQuerySymbol(MBABeginQuerySymbol msg)
    {
        // 查询symbol的历史数据 （当前 或前一交易日的 5秒 历史数据）
        if (symbol != null && msg != null)
        {
            oldContract = (curContract != null) ? curContract.clone() : null;
            curContract = symbol.getSymbolContract();
            symbol.reqRealTimePrice(oldContract, curContract);

            sSpyRealTimePnl.clearAllData();

            // 计算当前时间到开盘时间的时间间隔, 单位秒
            long duration = -1;
            String locatime = null;

            LocalDateTime curUsaOpenDateTime = getCurrentDayUSAOpenDateTime();
            LocalDateTime curUsaLocalDateTime = getCurrentAmericaLocalDateTime();
            // 如果现在是开盘时间，则取当前时间
            if (ifNowIsOpenTime() || (curUsaOpenDateTime != null && curUsaLocalDateTime.isBefore(curUsaOpenDateTime) &&
                                      curUsaLocalDateTime.plusMinutes(10).isAfter(curUsaOpenDateTime)))
            {
                openUsaDateTime = getCurrentDayUSAOpenDateTime();
                closeUsaDateTime = getCurrentDayUSACloseDateTime();

                duration = getLastOpenTimeSeconds();
                barSize = getBarSizebyDurationSeconds(duration);
                locatime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss"));
            }
            else // 如果不是开盘时间，则取上一天的开盘时间/
            {
                // 获取指定天数之前的开盘的本地时间, 参数 lastDay 是表示之前多少天
                Pair<LocalDateTime, LocalDateTime> lastUsaOpenCloseTime = getLastDayUSAOpenDateTime();
                LocalDateTime usaCurDateTime = getCurrentAmericaLocalDateTime();
                if (usaCurDateTime.isBefore(lastUsaOpenCloseTime.getKey()))
                {
                    lastUsaOpenCloseTime = getUSAOpenDateTimeByLastDay(1);
                }
                LocalDateTime localCloseDateTime = usaChangeToLocalDateTime(lastUsaOpenCloseTime.getValue());
                openUsaDateTime = lastUsaOpenCloseTime.getKey();
                closeUsaDateTime = lastUsaOpenCloseTime.getValue();

                duration = 30000;
                barSize = Types.BarSize._30_secs;
                locatime = localCloseDateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss"));
            }
            // 注意：设置X轴需要用美国时间
            setXRange(openUsaDateTime, closeUsaDateTime);

            // 注意：查询历史数据需要用本地时间
            symbol.getHistoricDatasAndProcess(curContract,
                                              locatime,
                                              duration,
                                              Types.DurationUnit.SECOND,
                                              barSize,
                                              getDataFinishProcess());


        }
    }


    private ProcessInAWT getDataFinishProcess()
    {
        ProcessInAWT processInAWT = new ProcessInAWT()
        {
            @Override
            public void successInAWT(Object param)
            {
                historicalDataList = (List) param;
                // 根据BarSize来获取 “ 开价，最高价，最低价，收价’的时间间隔
                int stepSec = getStepSecond(barSize);

                // 获取历史数据中最低和最高值
                Pair lowHighPair = getLowHighPair(historicalDataList);
                if (lowHighPair != null)
                {
                    price_low = (Double) lowHighPair.getKey();
                    price_high = (Double) lowHighPair.getValue();
                    Double meg = (price_high - price_low) * 0.1;
                    sSpyRealTimePnl.setYRange(price_low - meg, price_high + meg);
                }

                for (MBAHistoricalData historicalData : historicalDataList)
                {
                    LocalDateTime usaDateTime = getUSADateTimeByEpochSecond(historicalData.date);
                    Double[] val = new Double[4];
                    val[0] = historicalData.open;
                    val[1] = historicalData.high;
                    val[2] = historicalData.low;
                    val[3] = historicalData.close;

                    for (int i = 0; i < 4; i++)
                    {
                        usaDateTime.plusSeconds(i * stepSec);
                        Date date = changeToDate(usaDateTime);
                        sSpyRealTimePnl.addValue(date, val[i]);
                    }
                }
                hasDarwHistory = ifNowIsOpenTime() ? false : true;
            }

            @Override
            public void failedInAWT(Object param)
            {
                super.failedInAWT(param);
            }
        };
        return processInAWT;
    }


    // symbolContract 实时价格过滤器
    public static class rcvContractRealTimePriceFilter implements IMessageFilter<ContractRealTimeInfo>
    {
        @Override
        public boolean accepts(ContractRealTimeInfo msg, SubscriptionContext subscriptionContext)
        {
            return msg != null && curContract != null && msg.contract.conid() == curContract.conid() &&
                   msg.tickType == TickType.LAST && ifNowIsOpenTime();
        }
    }

    @Handler(filters = {@Filter(rcvContractRealTimePriceFilter.class)})
    private void processContractRealTimePrice(ContractRealTimeInfo msg)
    {
        if (msg != null)
        {
            if (hasDarwHistory)
            {
                openUsaDateTime = getCurrentDayUSAOpenDateTime();
                closeUsaDateTime = getCurrentDayUSACloseDateTime();
                setXRange(openUsaDateTime, closeUsaDateTime);
                hasDarwHistory = false;
            }

            Range yRange = sSpyRealTimePnl.getYRange();
            Double lower = yRange.getLowerBound();
            Double upper = yRange.getUpperBound();

            if (yRange == null || msg.lastPrice < lower || (msg.lastPrice - lower) > 0.6 || msg.lastPrice > upper ||
                (upper - msg.lastPrice) > 0.6)
            {
                sSpyRealTimePnl.setYRange(msg.lastPrice - 0.5, msg.lastPrice + 0.5);
            }
            Date date = changeToDate(getCurrentAmericaLocalDateTime());
            sSpyRealTimePnl.addValue(date, msg.lastPrice);

        }
    }

    // 根据BarSize来获取 “ 开价，最高价，最低价，收价’的时间间隔
    private int getStepSecond(Types.BarSize barSize)
    {
        int stepSec = 1;
        if (Types.BarSize._5_secs == barSize)
        {
            stepSec = 1;
        }
        else if (Types.BarSize._10_secs == barSize)
        {
            stepSec = 2;
        }
        else if (Types.BarSize._15_secs == barSize)
        {
            stepSec = 3;
        }
        else if (Types.BarSize._30_secs == barSize)
        {
            stepSec = 6;
        }
        return stepSec;
    }


}
