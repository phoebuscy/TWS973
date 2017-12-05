package com.view.panel.smallPanel;

import com.commdata.mbassadorObj.MBABeginQuerySymbol;
import com.commdata.mbassadorObj.MBAHistoricalData;
import com.commdata.mbassadorObj.MBASymbolRealPrice;
import com.commdata.pubdata.ProcessInAWT;
import com.dataModel.SDataManager;
import com.dataModel.Symbol;
import com.ib.client.Types;
import com.utils.TConst;
import com.utils.TMbassadorSingleton;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javafx.util.Pair;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import net.engio.mbassy.listener.Filter;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.IMessageFilter;
import net.engio.mbassy.subscription.SubscriptionContext;
import static com.utils.SUtil.getCurrentAmericaLocalDateTime;
import static com.utils.SUtil.getDimension;
import static com.utils.SUtil.getLastDayUSAOpenDateTime;
import static com.utils.SUtil.getUSADateTimeByEpochSecond;
import static com.utils.SUtil.getUSAOpenDateTimeByLastDay;
import static com.utils.SUtil.ifNowIsOpenTime;
import static com.utils.SUtil.usaChangeToLocalDateTime;
import static com.utils.TConst.DATAMAAGER_BUS;
import static com.utils.TConst.SYMBOL_BUS;
import static com.utils.TFileUtil.getConfigValue;
import static com.utils.TPubUtil.crtContract;
import static com.utils.TPubUtil.notNullAndEmptyCollection;

/**
 * Created by 123 on 2016/12/24.
 */
public class SSymbolePanel extends JPanel
{
    private Component parentWin;
    private Dimension parentDimension;
    private JLabel symbolStr = new JLabel("Symbol:");
    private JTextField symbolText = new JTextField("spy", 10);
    private JButton btnQuery = crtQueryBtn();                  // 查询实时价格按钮
    private JLabel price = new JLabel("225.71    +0.33    +0.15%:");

    private List<MBAHistoricalData> historicalDataList = new ArrayList<>();

    private double realTimePrice = 0.0; // 当前实时价格
    private double todayOpenPrice = 258.2; // 今开价格
    private double yesterdayClosePrice = 261.2;  // 昨收价格


    private final String curPriceStr = getConfigValue("current.price", TConst.CONFIG_I18N_FILE);  // 最新价
    private final String yesterdayCloseStr = getConfigValue("yesterday.close", TConst.CONFIG_I18N_FILE);  // 昨收
    private final String todayOpenStr = getConfigValue("today.open", TConst.CONFIG_I18N_FILE); // 今开
    private final String zdeStr = getConfigValue("zde", TConst.CONFIG_I18N_FILE); // 涨跌额
    private final String zdfStr = getConfigValue("zdf", TConst.CONFIG_I18N_FILE);  // 涨跌幅

    private Symbol symbol = SDataManager.getInstance().getSymbol();

    public SSymbolePanel(Component parentWin)
    {
        setBackground(Color.white);
        this.parentWin = parentWin;
        parentDimension = parentWin.getSize();
        setDimension();
        buildGUI();
        setPrice(realTimePrice, todayOpenPrice, yesterdayClosePrice);

        // 订阅消息总线名称为 SYMBOL_BUS 的 消息
        TMbassadorSingleton.getInstance(SYMBOL_BUS).subscribe(this);
        TMbassadorSingleton.getInstance(DATAMAAGER_BUS).subscribe(this);
    }

    private void setDimension()
    {
        setSize(getDimension(parentDimension, 1.0, 0.1));
    }


    private void buildGUI()
    {
        setLayout(new FlowLayout(FlowLayout.LEFT, 15, 0));
        add(symbolStr);
        add(symbolText);
        add(btnQuery);
        add(price);
    }

    public void setPrice(double realTimePrice, double todayOpenPrice, double yesterdayClosePrice)
    {
        double add = realTimePrice - yesterdayClosePrice;
        double addRate = (realTimePrice != 0.0) ? add / realTimePrice * 100 : 0.0;

        String str = String.format("%s:  %.2f    %s:  %.2f   %s: %.2f%%    %s: %.2f   %s: %.2f",
                                   curPriceStr,
                                   realTimePrice,
                                   zdeStr,
                                   add,
                                   zdfStr,
                                   addRate,
                                   todayOpenStr,
                                   todayOpenPrice,
                                   yesterdayCloseStr,
                                   yesterdayClosePrice);
        // 当前价格 258.39   涨 1.23  涨幅 5.2%  今开 259.5  昨收 258.5
        price.setText(str);
        // “dialog”代表字体，1代表样式(1是粗体，0是平常的）15是字号设置字体
        price.setFont(new java.awt.Font("Dialog", 1, 15));
        Color backColor = (realTimePrice == 0.0) ? Color.black : (add > 0.0 ? Color.RED : Color.blue);
        price.setForeground(backColor);
    }

    // 开始查询symbolText中的实时价格
    private JButton crtQueryBtn()
    {
        JButton btnQuery = new JButton(getConfigValue("query", TConst.CONFIG_I18N_FILE));
        btnQuery.addActionListener(new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                realTimePrice = 0D;
                todayOpenPrice = 0D;
                yesterdayClosePrice = 0D;
                setPrice(realTimePrice, todayOpenPrice, yesterdayClosePrice);

                // 1: 查询当前symbol的实时价格
                // 2: 查询期权链
                // 3：默认查询最近期权链数据
                if (symbol != null)
                {
                    String symbolVal = symbolText.getText().trim();
                    symbol.cancelQuerySymbolRealPrice();
                    symbol.setSymbolVal(symbolVal);
                    symbol.querySymbolRealPrice();

                    if (!ifNowIsOpenTime()) // 如果现在不是开盘时间，则需要查询上一次开盘时间的历史数据来显示
                    {
                        reqLastOneDayHistoricData();
                    }
                    // 发送开始查询symbol消息
                    TMbassadorSingleton.getInstance(SYMBOL_BUS).publish(new MBABeginQuerySymbol(symbolVal));
                }
            }
        });
        return btnQuery;
    }

    private void reqLastOneDayHistoricData()
    {
        // 获取指定天数之前的开盘的本地时间, 参数 lastDay 是表示之前多少天
        Pair<LocalDateTime, LocalDateTime> lastUsaOpenCloseTime = getLastDayUSAOpenDateTime();
        LocalDateTime usaCurDateTime = getCurrentAmericaLocalDateTime();
        if (usaCurDateTime.isBefore(lastUsaOpenCloseTime.getKey()))
        {
            lastUsaOpenCloseTime = getUSAOpenDateTimeByLastDay(1);
        }
        LocalDateTime localCloseDateTime = usaChangeToLocalDateTime(lastUsaOpenCloseTime.getValue());
        localCloseDateTime = localCloseDateTime.plusMinutes(60);


        long duration = 1;
        Types.BarSize barSize = Types.BarSize._30_mins;
        String localCloseTimeStr = localCloseDateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss"));

        symbol.getHistoricDatasAndProcess(crtContract(symbol.getSymbolVal()),
                                          localCloseTimeStr,
                                          duration,
                                          Types.DurationUnit.DAY,
                                          barSize,
                                          getHistoricDataFinishProcess());

    }


    private ProcessInAWT getHistoricDataFinishProcess()
    {
        ProcessInAWT processInAWT = new ProcessInAWT()
        {
            @Override
            public void successInAWT(Object param)
            {
                historicalDataList = (List) param;
                Pair<LocalDateTime, LocalDateTime> lastUsaOpenCloseTime = getLastDayUSAOpenDateTime();
                LocalDateTime openDateTime = lastUsaOpenCloseTime.getKey();
                LocalDateTime closeDateTime = lastUsaOpenCloseTime.getValue();

                if (notNullAndEmptyCollection(historicalDataList))
                {
                    for (MBAHistoricalData historicalData : historicalDataList)
                    {
                        LocalDateTime usaDateTime = getUSADateTimeByEpochSecond(historicalData.date);
                        if (openDateTime.equals(usaDateTime))
                        {
                            todayOpenPrice = historicalData.open;
                        }
                        if (closeDateTime.equals(usaDateTime))
                        {
                            realTimePrice = historicalData.open;
                        }
                    }
                }
                symbol.setSymbolRealPrice(realTimePrice);
                symbol.setSymbolTodayOpenPrice(todayOpenPrice);
                symbol.setSymbolYesterdayClosePrice(todayOpenPrice);  // 此处把昨天收盘价用今天的开盘价代替
                yesterdayClosePrice = todayOpenPrice;
                setPrice(realTimePrice, todayOpenPrice, yesterdayClosePrice);
            }

            @Override
            public void failedInAWT(Object param)
            {
                super.failedInAWT(param);
            }
        };
        return processInAWT;
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

    // 实时价格消息处理器
    @Handler(filters = {@Filter(realPriceStatusFilter.class)})
    private void getRealPrice(MBASymbolRealPrice msg)
    {
        todayOpenPrice = symbol.getSymbolTodayOpenPrice();
        yesterdayClosePrice = symbol.getSymbolYesterdayClosePrice();
        setPrice(msg.symbolRealPrice, todayOpenPrice, yesterdayClosePrice);
    }



}
