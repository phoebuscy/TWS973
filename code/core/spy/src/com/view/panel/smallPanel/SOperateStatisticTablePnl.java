package com.view.panel.smallPanel;

import com.commdata.mbassadorObj.MBAPortFolio;
import com.commdata.mbassadorObj.MBAtickPrice;
import com.dataModel.SDataManager;
import com.dataModel.Symbol;
import com.ib.client.Contract;
import com.ib.client.TickType;
import com.ib.client.Types;
import com.table.SOperateStatisticTable;
import com.utils.TMbassadorSingleton;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import net.engio.mbassy.listener.Filter;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.IMessageFilter;
import net.engio.mbassy.subscription.SubscriptionContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import static com.utils.SUtil.getDimension;
import static com.utils.TConst.DATAMAAGER_BUS;
import static com.utils.TConst.SYMBOL_BUS;

/**
 * Created by caiyong on 2016/12/25.
 */
public class SOperateStatisticTablePnl extends JPanel
{
    private static Logger LogApp = LogManager.getLogger("applog");

    private Component parentWin;
    private Dimension parentDimension;

    private Symbol symbol = SDataManager.getInstance().getSymbol();

    private SOperateStatisticTable table;
    // 查询实时价格的 reqid和 MBAPortFolio的map
    private static Map<Integer, MBAPortFolio> topMktDataReqID2MBAPortFolioMap = new HashMap<>();


    public SOperateStatisticTablePnl(Component parentWin)
    {
        setBackground(Color.blue);
        this.parentWin = parentWin;
        parentDimension = parentWin.getSize();
        setDimension();
        buildGUI();


        // 订阅消息总线名称为 DATAMAAGER_BUS 的 消息
        TMbassadorSingleton.getInstance(SYMBOL_BUS).subscribe(this);
        TMbassadorSingleton.getInstance(DATAMAAGER_BUS).subscribe(this);
    }

    private void setDimension()
    {
        setSize(getDimension(parentDimension, 0.5, 0.8));
    }

    private void buildGUI()
    {
        setLayout(new BorderLayout());
        table = new SOperateStatisticTable();
        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);
        add(scrollPane);
        setPreferredSize(getDimension(parentDimension, 0.6, 0.5));
    }


    // 接收账户信息过滤器 : 只显示SPY的OPT
    static public class portFolioDataFilter implements IMessageFilter<MBAPortFolio>
    {
        @Override
        public boolean accepts(MBAPortFolio msg, SubscriptionContext subscriptionContext)
        {
            return msg != null && isSpyOpt(msg);
        }
    }

    @Handler(filters = {@Filter(portFolioDataFilter.class)})
    private void processPortFolioData(MBAPortFolio msg)
    {
        updateMBAPortFolioInMap(msg);
        List<Object> rowDatas = makeRowData(msg);
        table.updateData(msg, rowDatas);

        // 查询期权实时价格
        queryRealTimePriceMktData(msg);
    }

    // 查询期权实时价格
    private void queryRealTimePriceMktData(MBAPortFolio msg)
    {
        if (msg != null)
        {
            if (msg.isClose())
            {
                symbol.cancelMktData(getReqIdOfMBAPortFolio(msg));
            }
            else if (!topMktDataReqID2MBAPortFolioMap.containsValue(msg))
            {
                msg.contract.exchange("SMART");
                int reqid = symbol.reqOptionMktData(msg.contract);
                topMktDataReqID2MBAPortFolioMap.put(reqid, msg);
            }
        }
    }

    private void updateMBAPortFolioInMap(MBAPortFolio msg)
    {
        if (topMktDataReqID2MBAPortFolioMap != null && msg != null)
        {
            for (Map.Entry<Integer, MBAPortFolio> entry : topMktDataReqID2MBAPortFolioMap.entrySet())
            {
                if (entry.getValue().equals(msg))
                {
                    entry.setValue(msg);
                }
            }
        }
    }

    private Integer getReqIdOfMBAPortFolio(MBAPortFolio msg)
    {
        if (topMktDataReqID2MBAPortFolioMap != null && msg != null)
        {
            for (Map.Entry<Integer, MBAPortFolio> entry : topMktDataReqID2MBAPortFolioMap.entrySet())
            {
                if (entry.getValue().equals(msg))
                {
                    return entry.getKey();
                }
            }
        }
        return -1;
    }


    private List<Object> makeRowData(MBAPortFolio msg)
    {
        List<Object> rowData = new ArrayList<>();
        if (msg != null)
        {
            String symbol = makeSymbol(msg); // 标的
            String currentPrice = makeCurrentPrice(msg);//最新价
            String averageBuyPrice = makeAverageBuyPrice(msg);// 持仓价
            String count = makeCount(msg); // 数量
            String marketValue = makeMarketValue(msg);//  市值
            String zdf = makeZdf(msg); // 涨跌幅
            String saleprice = makeSaleprice(msg); //卖价
            String yinorkui = makeYinorkui(msg); // 盈亏/
            String time = makeTime(msg); //时间

            rowData.add(symbol);
            rowData.add(currentPrice);
            rowData.add(averageBuyPrice);
            rowData.add(count);
            rowData.add(marketValue);
            rowData.add(zdf);
            rowData.add(saleprice);
            rowData.add(yinorkui);
            rowData.add(time);
        }
        return rowData;
    }

    private String makeSymbol(MBAPortFolio msg)
    {
        if (msg != null && msg.contract != null)
        {
            Contract cntt = msg.contract;
            String empt = "/";

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(cntt.symbol());
            if (cntt.secType() == Types.SecType.OPT)
            {
                stringBuilder.append(empt);
                if (cntt.lastTradeDateOrContractMonth() != null)
                {
                    stringBuilder.append(cntt.lastTradeDateOrContractMonth());
                    stringBuilder.append(empt);
                }
                if (Double.compare(cntt.strike(), 0D) == 1)
                {
                    stringBuilder.append(cntt.strike());
                    stringBuilder.append(empt);
                }
                if (cntt.getRight() != null)
                {
                    stringBuilder.append(cntt.getRight());
                }
            }
            return stringBuilder.toString();
        }
        return "";
    }

    private String makeCurrentPrice(MBAPortFolio msg)
    {
        if (msg != null)
        {
            return String.format("%.3f", msg.marketPrice);
        }
        return "";
    }

    private String makeAverageBuyPrice(MBAPortFolio msg)
    {
        if (msg != null && Double.compare(msg.position, 0D) == 1)
        {
            return String.format("%.3f", msg.averageCost / 100D);
        }
        return "";
    }

    private String makeCount(MBAPortFolio msg)
    {
        if (msg != null && Double.compare(msg.position, 0D) == 1)
        {
            return String.format("%.0f", msg.position);
        }
        return "";
    }

    private String makeMarketValue(MBAPortFolio msg)
    {
        if (msg != null)
        {
            return String.format("%.1f", msg.position * msg.marketPrice * 100D);// 市场价不用  msg.marketValue，用数量乘以均价;
        }
        return "";
    }

    private String makeZdf(MBAPortFolio msg) // 涨跌幅
    {
        if (msg != null && Double.compare(msg.position, 0D) == 1)
        {
            double averavePrice = msg.averageCost / 100D;
            double diff = msg.marketPrice - averavePrice;
            Double percent = diff / averavePrice;
            // return String.valueOf(percent);
            return String.format("%.2f", percent);
        }
        return "";
    }

    private String makeSaleprice(MBAPortFolio msg)
    {
        if (msg != null)
        {

        }
        return "";
    }


    private String makeYinorkui(MBAPortFolio msg)
    {
        if (msg != null)
        {
            if (msg.isClose()) // 平仓了的用 realizedPNL
            {
                return String.format("%.1f", msg.realizedPNL);
            }
            else // 未平仓的需要计算
            {
                return String.format("%.1f", (msg.marketPrice - (msg.averageCost / 100D)) * msg.position * 100);
            }
        }
        return "";
    }

    private String makeTime(MBAPortFolio msg)
    {
        if (msg != null)
        {
            if (Double.compare(msg.position, 0D) == 0) // 表示平仓了
            {
                return LocalDateTime.now().toString();
            }
        }
        return "";
    }


    // 接收查询symbol的实时价格的消息过滤器 （注意：此处用的是最新买价做实时价格）
    static public class recvRealTimePriceFilter implements IMessageFilter<MBAtickPrice>
    {
        @Override
        public boolean accepts(MBAtickPrice msg, SubscriptionContext subscriptionContext)
        {
            if (msg != null && topMktDataReqID2MBAPortFolioMap.containsKey(msg.tickerId))
            {
                TickType tickType = TickType.get(msg.field);
                // 最新买价要大于0时用买价
                return (Double.compare(msg.price, 0D) == 1 &&
                        (TickType.BID.equals(tickType) || TickType.DELAYED_BID.equals(tickType)));
            }
            return false;
        }
    }

    // 实时消息处理器
    @Handler(filters = {@Filter(recvRealTimePriceFilter.class)})
    private void processRealTimePrice(MBAtickPrice msg)
    {
        if (msg != null)
        {
            MBAPortFolio mbaPortFolio = topMktDataReqID2MBAPortFolioMap.get(msg.tickerId);
            if (!mbaPortFolio.isClose()) // 已经平仓的则不处理
            {
                mbaPortFolio.marketPrice = msg.price;
                List<Object> rowDatas = makeRowData(mbaPortFolio);
                table.updateData(mbaPortFolio, rowDatas);
            }
        }
    }

    private static boolean isSpyOpt(MBAPortFolio msg)
    {
        return msg != null && Types.SecType.OPT.equals(msg.contract.secType());
    }

}
