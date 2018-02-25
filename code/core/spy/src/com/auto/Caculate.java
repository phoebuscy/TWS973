package com.auto;

import com.commdata.mbassadorObj.MBACaculateTxtInfo;
import com.commdata.mbassadorObj.MBACalculateBuyOrSaleNotice;
import com.commdata.mbassadorObj.MBAHistoricalData;
import com.commdata.pubdata.ContractRealTimeInfo;
import com.commdata.pubdata.ProcessInAWT;
import com.dataModel.SDataManager;
import com.dataModel.Symbol;
import com.ib.client.Contract;
import com.ib.client.TickType;
import com.ib.client.Types;
import com.utils.TMbassadorSingleton;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.util.Pair;
import net.engio.mbassy.listener.Filter;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.IMessageFilter;
import net.engio.mbassy.subscription.SubscriptionContext;
import static com.utils.SUtil.changeToDate;
import static com.utils.SUtil.getBarSizebyDurationSeconds;
import static com.utils.SUtil.getCurrentAmericaLocalDateTime;
import static com.utils.SUtil.getCurrentDayUSACloseDateTime;
import static com.utils.SUtil.getCurrentDayUSAOpenDateTime;
import static com.utils.SUtil.getLastDayUSAOpenDateTime;
import static com.utils.SUtil.getLastOpenTimeSeconds;
import static com.utils.SUtil.getUSADateTimeByEpochSecond;
import static com.utils.SUtil.getUSAOpenDateTimeByLastDay;
import static com.utils.SUtil.ifNowIsOpenTime;
import static com.utils.SUtil.isIntNumeric;
import static com.utils.SUtil.isOpenTime;
import static com.utils.SUtil.usaChangeToLocalDateTime;
import static com.utils.TConst.CALCULATE_BUS;
import static com.utils.TConst.DATAMAAGER_BUS;
import static com.utils.TConst.REALTIMEPRICEMGR_BUS;
import static com.utils.TConst.SYMBOL_BUS;
import static com.utils.TPubUtil.notNullAndEmptyCollection;

/**
 * 该类负责计算买卖点，提供买卖标的，发通知给 BuyOrSale模块进行买卖
 * <p>
 * 通过发送消息方式进行通知
 */

public class Caculate
{

    private static boolean isRuning = false;
    private Symbol symbol = SDataManager.getInstance().getSymbol();
    private static Caculate instance = new Caculate();
    private static Contract symbolContract = null;

    private List<MBAHistoricalData> historicalDataList = new ArrayList<>();
    private Double[] realTimePriceArry = new Double[23400];  // 开市时间9.30 到 下午4点 的秒数：  6.5 * 3600 = 23400 秒

    ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();

    private Caculate()
    {

        // 订阅消息总线名称为 SYMBOL_BUS 的 消息
        TMbassadorSingleton.getInstance(SYMBOL_BUS).subscribe(this);
        TMbassadorSingleton.getInstance(DATAMAAGER_BUS).subscribe(this);
        TMbassadorSingleton.getInstance(REALTIMEPRICEMGR_BUS).subscribe(this);

        TimerTask timerTask = new TimerTask(2000); // 任务需要 2000 ms 才能执行完毕
        System.out.printf("起始时间：%s\n\n", new SimpleDateFormat("HH:mm:ss").format(new Date()));
        // 延时 1 秒后，按 3 秒的周期执行任务
        timer.scheduleAtFixedRate(timerTask, 1000, 3000, TimeUnit.MILLISECONDS);
    }

    public static Caculate getInstance()
    {
        return instance;
    }

    public void beginCaculate()
    {
        isRuning = true;
        symbolContract = symbol.getSymbolContract();
        symbol.reqRealTimePrice(symbolContract);
        getHistoricData();
    }

    // 停止计算
    public void stop()
    {
        isRuning = false;
        TMbassadorSingleton.getInstance(CALCULATE_BUS).publish(new MBACaculateTxtInfo("caculate stop XXXXXXX"));

    }

    // 发布买入通知
    public void publishBuyNotice(Types.Right right)
    {
        // 发布买入通知
        TMbassadorSingleton.getInstance(CALCULATE_BUS).publish(new MBACalculateBuyOrSaleNotice(Types.Action.BUY,
                                                                                               right));
    }

    // 发布卖出通知
    public void publishSaleNotice(Types.Right right)
    {
        TMbassadorSingleton.getInstance(CALCULATE_BUS).publish(new MBACalculateBuyOrSaleNotice(Types.Action.SELL,
                                                                                               right));
    }

    private static class TimerTask implements Runnable
    {
        private final SimpleDateFormat dateFormat;

        public TimerTask(int sleepTime)
        {
            dateFormat = new SimpleDateFormat("HH:mm:ss");
        }

        @Override
        public void run()
        {
            if (Caculate.isRuning)
            {
                TMbassadorSingleton.getInstance(CALCULATE_BUS).publish(new MBACaculateTxtInfo(
                        "任务开始，当前时间：" + dateFormat.format(new Date())));
            }
            else
            {
                TMbassadorSingleton.getInstance(CALCULATE_BUS).publish(new MBACaculateTxtInfo(
                        "任务未开始，当前时间：" + dateFormat.format(new Date())));
            }

        }
    }

    private void getHistoricData()
    {
        // 查询symbol的历史数据 （当前 或前一交易日的 5秒 历史数据）
        if (symbol != null)
        {
            // 计算当前时间到开盘时间的时间间隔, 单位秒
            long duration = -1;
            String locatime = null;

            LocalDateTime curUsaOpenDateTime = getCurrentDayUSAOpenDateTime();
            LocalDateTime curUsaLocalDateTime = getCurrentAmericaLocalDateTime();
            // 如果现在是开盘时间，则取当前时间
            LocalDateTime openUsaDateTime;
            LocalDateTime closeUsaDateTime;
            Types.BarSize barSize;
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


            // 注意：查询历史数据需要用本地时间
            symbol.getHistoricDatasAndProcess(symbolContract,
                                              locatime,
                                              duration,
                                              Types.DurationUnit.SECOND,
                                              barSize,
                                              getDataFinishProcess());


        }
    }


    // 把历史数据变成实时数据
    private ProcessInAWT getDataFinishProcess()
    {
        ProcessInAWT processInAWT = new ProcessInAWT()
        {
            @Override
            public void successInAWT(Object param)
            {
                historicalDataList = (List) param;
                // 根据BarSize来获取 “ 开价，最高价，最低价，收价’的时间间隔
                Types.BarSize barSize = getBarSizeByHistoricData(historicalDataList);
                if (barSize == null)
                {
                    return;
                }
                int stepSec = getStepSecond(barSize);

                for (MBAHistoricalData historicalData : historicalDataList)
                {
                    changeHistoricToRealTimeData(realTimePriceArry, historicalData, barSize);

                }
            }

            @Override
            public void failedInAWT(Object param)
            {
                super.failedInAWT(param);
            }
        };
        return processInAWT;
    }

    private void changeHistoricToRealTimeData(Double[] realTimePriceArry,
                                              MBAHistoricalData historicalData,
                                              Types.BarSize barSize)
    {


        LocalDateTime usaDateTime = getUSADateTimeByEpochSecond(historicalData.date);
        if(isOpenTime(usaDateTime))
        {
            Double[] val = new Double[4];
            val[0] = historicalData.open;
            val[1] = historicalData.high;
            val[2] = historicalData.low;
            val[3] = historicalData.close;
            for (int i = 0; i < 4; i++)
            {
                //  usaDateTime.plusSeconds(i * stepSec);
                Date date = changeToDate(usaDateTime);
            }
        }

    }

    private Types.BarSize getBarSizeByHistoricData(List<MBAHistoricalData> historicalDataList)
    {
        if (notNullAndEmptyCollection(historicalDataList) && historicalDataList.size() > 1)
        {
            if (isIntNumeric(historicalDataList.get(0).date) && isIntNumeric(historicalDataList.get(1).date))
            {
                Integer disSecond = Math.abs(Integer.valueOf(historicalDataList.get(1).date) -
                                             Integer.valueOf(historicalDataList.get(0).date));
                switch (disSecond)
                {
                    case 1:
                        return Types.BarSize._1_secs;
                    case 5:
                        return Types.BarSize._5_secs;
                    case 10:
                        return Types.BarSize._10_secs;
                    case 15:
                        return Types.BarSize._15_secs;
                    case 30:
                        return Types.BarSize._30_secs;
                    case 60:
                        return Types.BarSize._1_min;
                    case 2 * 60:
                        return Types.BarSize._2_mins;
                    case 3 * 60:
                        return Types.BarSize._3_mins;
                    case 5 * 60:
                        return Types.BarSize._5_mins;
                    case 15 * 60:
                        return Types.BarSize._15_mins;
                    case 20 * 60:
                        return Types.BarSize._20_mins;
                    case 30 * 60:
                        return Types.BarSize._30_mins;
                    case 1 * 3600:
                        return Types.BarSize._1_hour;
                    case 4 * 3600:
                        return Types.BarSize._4_hours;
                    case 24 * 3600:
                        return Types.BarSize._1_day;
                    case 7 * 24 * 3600:
                        return Types.BarSize._1_week;
                }
            }
        }
        return null;
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


    // symbolContract 实时价格过滤器
    public static class rcvContractRealTimePriceFilter implements IMessageFilter<ContractRealTimeInfo>
    {
        @Override
        public boolean accepts(ContractRealTimeInfo msg, SubscriptionContext subscriptionContext)
        {
            return msg != null && symbolContract != null && msg.contract.conid() == symbolContract.conid() &&
                   msg.tickType == TickType.LAST && ifNowIsOpenTime();
        }
    }

    @Handler(filters = {@Filter(rcvContractRealTimePriceFilter.class)})
    private void processContractRealTimePrice(ContractRealTimeInfo msg)
    {
        if (msg != null)
        {
            double lastPrice = msg.lastPrice;  // 当前实时价格
        }
    }


}
