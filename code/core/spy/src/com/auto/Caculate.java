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
 * ���ฺ����������㣬�ṩ������ģ���֪ͨ�� BuyOrSaleģ���������
 * <p>
 * ͨ��������Ϣ��ʽ����֪ͨ
 */

public class Caculate
{

    private static boolean isRuning = false;
    private Symbol symbol = SDataManager.getInstance().getSymbol();
    private static Caculate instance = new Caculate();
    private static Contract symbolContract = null;

    private List<MBAHistoricalData> historicalDataList = new ArrayList<>();
    private Double[] realTimePriceArry = new Double[23400];  // ����ʱ��9.30 �� ����4�� ��������  6.5 * 3600 = 23400 ��

    ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();

    private Caculate()
    {

        // ������Ϣ��������Ϊ SYMBOL_BUS �� ��Ϣ
        TMbassadorSingleton.getInstance(SYMBOL_BUS).subscribe(this);
        TMbassadorSingleton.getInstance(DATAMAAGER_BUS).subscribe(this);
        TMbassadorSingleton.getInstance(REALTIMEPRICEMGR_BUS).subscribe(this);

        TimerTask timerTask = new TimerTask(2000); // ������Ҫ 2000 ms ����ִ�����
        System.out.printf("��ʼʱ�䣺%s\n\n", new SimpleDateFormat("HH:mm:ss").format(new Date()));
        // ��ʱ 1 ��󣬰� 3 �������ִ������
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

    // ֹͣ����
    public void stop()
    {
        isRuning = false;
        TMbassadorSingleton.getInstance(CALCULATE_BUS).publish(new MBACaculateTxtInfo("caculate stop XXXXXXX"));

    }

    // ��������֪ͨ
    public void publishBuyNotice(Types.Right right)
    {
        // ��������֪ͨ
        TMbassadorSingleton.getInstance(CALCULATE_BUS).publish(new MBACalculateBuyOrSaleNotice(Types.Action.BUY,
                                                                                               right));
    }

    // ��������֪ͨ
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
                        "����ʼ����ǰʱ�䣺" + dateFormat.format(new Date())));
            }
            else
            {
                TMbassadorSingleton.getInstance(CALCULATE_BUS).publish(new MBACaculateTxtInfo(
                        "����δ��ʼ����ǰʱ�䣺" + dateFormat.format(new Date())));
            }

        }
    }

    private void getHistoricData()
    {
        // ��ѯsymbol����ʷ���� ����ǰ ��ǰһ�����յ� 5�� ��ʷ���ݣ�
        if (symbol != null)
        {
            // ���㵱ǰʱ�䵽����ʱ���ʱ����, ��λ��
            long duration = -1;
            String locatime = null;

            LocalDateTime curUsaOpenDateTime = getCurrentDayUSAOpenDateTime();
            LocalDateTime curUsaLocalDateTime = getCurrentAmericaLocalDateTime();
            // ��������ǿ���ʱ�䣬��ȡ��ǰʱ��
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
            else // ������ǿ���ʱ�䣬��ȡ��һ��Ŀ���ʱ��/
            {
                // ��ȡָ������֮ǰ�Ŀ��̵ı���ʱ��, ���� lastDay �Ǳ�ʾ֮ǰ������
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


            // ע�⣺��ѯ��ʷ������Ҫ�ñ���ʱ��
            symbol.getHistoricDatasAndProcess(symbolContract,
                                              locatime,
                                              duration,
                                              Types.DurationUnit.SECOND,
                                              barSize,
                                              getDataFinishProcess());


        }
    }


    // ����ʷ���ݱ��ʵʱ����
    private ProcessInAWT getDataFinishProcess()
    {
        ProcessInAWT processInAWT = new ProcessInAWT()
        {
            @Override
            public void successInAWT(Object param)
            {
                historicalDataList = (List) param;
                // ����BarSize����ȡ �� ���ۣ���߼ۣ���ͼۣ��ռۡ���ʱ����
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


    // ����BarSize����ȡ �� ���ۣ���߼ۣ���ͼۣ��ռۡ���ʱ����
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


    // symbolContract ʵʱ�۸������
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
            double lastPrice = msg.lastPrice;  // ��ǰʵʱ�۸�
        }
    }


}
