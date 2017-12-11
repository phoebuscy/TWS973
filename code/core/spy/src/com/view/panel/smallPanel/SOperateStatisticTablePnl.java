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


    // 接收账户信息过滤器
    static public class portFolioDataFilter implements IMessageFilter<MBAPortFolio>
    {
        @Override
        public boolean accepts(MBAPortFolio msg, SubscriptionContext subscriptionContext)
        {
            return msg != null;
        }
    }

    @Handler(filters = {@Filter(portFolioDataFilter.class)})
    private void processPortFolioData(MBAPortFolio msg)
    {
        List<Object> rowDatas = makeRowData(msg);
        table.updateData(msg, rowDatas);

        // 查询期权实时价格
        queryRealTimePriceMktData(msg);
    }


    // 查询期权实时价格
    private void queryRealTimePriceMktData(MBAPortFolio msg)
    {
        if (msg != null && !topMktDataReqID2MBAPortFolioMap.containsValue(msg))
        {
            msg.contract.exchange("SMART");
            int reqid = symbol.reqOptionMktData(msg.contract);
            topMktDataReqID2MBAPortFolioMap.put(reqid, msg);
        }
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
            if (msg.contract.secType() == Types.SecType.OPT)
            {
                return String.format("%.3f", msg.averageCost / msg.position);
            }
            else
            {
                return String.format("%.3f", msg.averageCost);
            }
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
            return String.format("%.3f", msg.marketValue);
        }
        return "";
    }

    private String makeZdf(MBAPortFolio msg)
    {
        if (msg != null && Double.compare(msg.position, 0D) == 1)
        {
            boolean isOpt = msg.contract.secType() == Types.SecType.OPT;
            double averavePrice = msg.averageCost / (isOpt ? msg.position : 1);
            double diff = msg.marketPrice - averavePrice;
            Double percent = diff / averavePrice;
            return String.valueOf(percent);
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
            return Double.compare(msg.realizedPNL, 0D) == 0 ? String.format("%.3f", msg.unrealizedPNL) : String.format(
                    "%.3f",
                    msg.realizedPNL);
        }
        return "";
    }

    private String makeTime(MBAPortFolio msg)
    {
        if (msg != null)
        {

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
                        (TickType.BID.equals(tickType) || TickType.DELAYED_BID.equals(tickType))) ||
                       (TickType.LAST.equals(tickType) || TickType.DELAYED_LAST.equals(tickType));

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
            mbaPortFolio.marketPrice = msg.price;
            mbaPortFolio.marketValue = mbaPortFolio.position * mbaPortFolio.marketPrice;
            if (Double.compare(mbaPortFolio.position, 0D) == 1 && Double.compare(mbaPortFolio.marketValue, 0D) == 1)
            {
                mbaPortFolio.unrealizedPNL =
                        (mbaPortFolio.marketPrice - mbaPortFolio.averageCost) * mbaPortFolio.position;
            }
            List<Object> rowDatas = makeRowData(mbaPortFolio);
            table.updateData(mbaPortFolio, rowDatas);
        }
    }

}
