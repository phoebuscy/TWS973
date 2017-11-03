package com.view.panel;

import com.dataModel.SDataManager;
import com.dataModel.Symbol;
import com.dataModel.mbassadorObj.MBABeginQuerySymbol;
import com.dataModel.mbassadorObj.MBAHistoricalData;
import com.dataModel.mbassadorObj.MBAHistoricalDataEnd;
import com.dataModel.mbassadorObj.MBASymbolRealPrice;
import com.ib.client.Types;
import com.utils.GBC;
import com.utils.SUtil;
import com.utils.TMbassadorSingleton;
import com.view.panel.smallPanel.SRealTimePnl;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JPanel;
import net.engio.mbassy.listener.Filter;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.IMessageFilter;
import net.engio.mbassy.subscription.SubscriptionContext;
import org.jfree.data.Range;
import static com.utils.SUtil.changeToDate;
import static com.utils.SUtil.getAmericaLocalDateTime;
import static com.utils.SUtil.getCurrentDayUSAOpenDateTime;
import static com.utils.SUtil.getUSADateTimeByEpochSecond;
import static com.utils.SUtil.ifNowIsOpenTime;
import static com.utils.TConst.DATAMAAGER_BUS;
import static com.utils.TConst.SYMBOL_BUS;
import static com.utils.TStringUtil.notNullAndEmptyStr;

/**
 * Created by 123 on 2016/12/18.
 */
public class SRealTimePicturePnl extends JPanel
{
    private Double price_low = 0D;
    private Double price_high = 0D;
    private static int reqHistoritDataReqid = -1;
    private Types.BarSize barSize = Types.BarSize._10_secs;
    private List<MBAHistoricalData> historicalDataList = new ArrayList<>();
    private Symbol symbol = SDataManager.getInstance().getSymbol();
    private SRealTimePnl sSpyRealTimePnl = new SRealTimePnl("spy", new Dimension(100, 200));
    private SRealTimePnl sCallRealTimePnl = new SRealTimePnl("Call", new Dimension(100, 150));
    private SRealTimePnl sPutRealTimePnl = new SRealTimePnl("Put", new Dimension(100, 150));

    private Dimension parentDimension;

    public SRealTimePicturePnl(Component parentWin)
    {
        setLayout(new GridBagLayout());
        add(sSpyRealTimePnl, new GBC(0, 0).setWeight(50, 50).setFill(GBC.BOTH));
        add(sCallRealTimePnl, new GBC(0, 1).setWeight(50, 10).setFill(GBC.BOTH));
        add(sPutRealTimePnl, new GBC(0, 2).setWeight(50, 10).setFill(GBC.BOTH));

        List<Date> beginEndDateLst = SUtil.getBeginEndDate();
        Date beginDate = beginEndDateLst.get(0);
        Date endDate = beginEndDateLst.get(1);
        sSpyRealTimePnl.setXRange(beginDate, endDate);

        //   setBackground(Color.cyan);
        parentDimension = parentWin.getSize();
        setDimension();

        // 订阅消息总线名称为 DATAMAAGER_BUS 的 消息
        TMbassadorSingleton.getInstance(SYMBOL_BUS).subscribe(this);
        TMbassadorSingleton.getInstance(DATAMAAGER_BUS).subscribe(this);
    }

    private void setDimension()
    {
        setSize(SUtil.getDimension(parentDimension, 0.5, 1.0));
    }

    // 接收开始查询symbol的消息
    static public class beginQuerySymbolFilter implements IMessageFilter<MBABeginQuerySymbol>
    {
        @Override
        public boolean accepts(MBABeginQuerySymbol msg, SubscriptionContext subscriptionContext)
        {
            return notNullAndEmptyStr(msg.getSymbol());
        }
    }

    // 处理始查询symbol消息
    @Handler(filters = {@Filter(beginQuerySymbolFilter.class)})
    private void processBeginQuerySymbol(MBABeginQuerySymbol msg)
    {
        // 查询symbol的历史数据 （当前 或前一交易日的 5秒 历史数据）
        if (symbol != null && notNullAndEmptyStr(msg.getSymbol()))
        {
            // 计算当前时间到开盘时间的时间间隔, 单位秒
            long duration = getLastOpenTimeSeconds();
            barSize = getBarSizebyDurationSeconds(duration);
            String locatime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss"));
            reqHistoritDataReqid = symbol.reqHistoryDatas(msg.getSymbol(),
                                                          locatime,
                                                          duration,
                                                          Types.DurationUnit.SECOND,
                                                          barSize);
        }
    }

    // 接收历史数据消息过滤器
    static public class historicDataFilter implements IMessageFilter<MBAHistoricalData>
    {
        @Override
        public boolean accepts(MBAHistoricalData msg, SubscriptionContext subscriptionContext)
        {
            return msg.reqId == reqHistoritDataReqid;
        }
    }

    @Handler(filters = {@Filter(historicDataFilter.class)})
    private void getHistoricalData(MBAHistoricalData msg)
    {
        historicalDataList.add(msg);
    }

    // 接收历史数据消息结束
    static public class historicDataEndFilter implements IMessageFilter<MBAHistoricalDataEnd>
    {
        @Override
        public boolean accepts(MBAHistoricalDataEnd msg, SubscriptionContext subscriptionContext)
        {
            return msg.reqId == reqHistoritDataReqid;
        }
    }

    // 处理历史数据消息介绍
    @Handler(filters = {@Filter(historicDataEndFilter.class)})
    private void processHistoricDataEnd(MBAHistoricalDataEnd msg)
    {
        // 取消获取历史数据申请
        symbol.cancelReqHistoricalData(reqHistoritDataReqid);

        // 根据BarSize来获取 “ 开价，最高价，最低价，收价’的时间间隔
        int stepSec = getStepSecond(barSize);

        for (MBAHistoricalData historicalData : historicalDataList)
        {
            LocalDateTime usaDateTime = getUSADateTimeByEpochSecond(historicalData.date);
            Double[] val = new Double[4];
            val[0] = historicalData.open;
            val[1] = historicalData.high;
            val[2] = historicalData.low;
            val[3] = historicalData.close;

            if (price_high == 0D)
            {
                price_low = historicalData.low;
                price_high = historicalData.high;
            }
            else
            {
                Double old_low = price_low;
                Double old_high = price_high;
                price_low = historicalData.low < price_low ? historicalData.low : price_low;
                price_high = historicalData.high > price_high ? historicalData.high : price_high;
            }
            //////////
            Range yRange = sSpyRealTimePnl.getYRange();

            Double lower = yRange.getLowerBound();
            Double upper = yRange.getUpperBound();

            if (price_low < lower || (price_low - lower) > 0.6 || price_high > upper || (upper - price_high) > 0.6)
            {
                sSpyRealTimePnl.setYRange(historicalData.low - 0.5, historicalData.high + 0.5);
            }
            /////////
            for (int i = 0; i < 4; i++)
            {
                usaDateTime.plusSeconds(i * stepSec);
                Date date = changeToDate(usaDateTime);
                sSpyRealTimePnl.addValue(date, val[i]);
            }
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


    // 接收实时价格的消息过滤器
    static public class realPriceStatusFilter implements IMessageFilter<MBASymbolRealPrice>
    {
        @Override
        public boolean accepts(MBASymbolRealPrice msg, SubscriptionContext subscriptionContext)
        {
            return msg != null;
        }
    }

    // 连接消息处理器
    @Handler(filters = {@Filter(realPriceStatusFilter.class)})
    private void getRealPrice(MBASymbolRealPrice msg)
    {
        Range yRange = sSpyRealTimePnl.getYRange();

        Double lower = yRange.getLowerBound();
        Double upper = yRange.getUpperBound();

        if (yRange == null || msg.symbolRealPrice < lower || (msg.symbolRealPrice - lower) > 0.6 ||
            msg.symbolRealPrice > upper || (upper - msg.symbolRealPrice) > 0.6)
        {
            sSpyRealTimePnl.setYRange(msg.symbolRealPrice - 0.5, msg.symbolRealPrice + 0.5);
        }
        Date date = changeToDate(getAmericaLocalDateTime());
        sSpyRealTimePnl.addValue(date, msg.symbolRealPrice);

    }

    // 计算当前时间到开盘时间的时间间隔, 单位秒
    private long getLastOpenTimeSeconds()
    {
        if (ifNowIsOpenTime())
        {
            LocalDateTime usaOpentTime = getCurrentDayUSAOpenDateTime();
            LocalDateTime curUsaTime = getAmericaLocalDateTime();
            Duration duration = Duration.between(usaOpentTime, curUsaTime);
            return duration.getSeconds();
        }
        return -1;
    }

    // 根据时间间隔 获取到 BarSize
    private Types.BarSize getBarSizebyDurationSeconds(long seconds)
    {
        if (seconds <= 10000)
        {
            return Types.BarSize._5_secs;
        }
        else if (seconds <= 20000)
        {
            return Types.BarSize._10_secs;
        }
        else if (seconds <= 30000)
        {
            return Types.BarSize._15_secs;
        }
        else
        {
            return Types.BarSize._30_secs;
        }

    }


}
