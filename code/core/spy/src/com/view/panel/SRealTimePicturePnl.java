package com.view.panel;

import com.dataModel.SDataManager;
import com.dataModel.Symbol;
import com.dataModel.mbassadorObj.MBABeginQuerySymbol;
import com.dataModel.mbassadorObj.MBASymbolRealPrice;
import com.utils.GBC;
import com.utils.SUtil;
import com.utils.TMbassadorSingleton;
import com.view.panel.smallPanel.SRealTimePnl;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
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
import static com.utils.TConst.SYMBOL_BUS;
import static com.utils.TStringUtil.notNullAndEmptyStr;

/**
 * Created by 123 on 2016/12/18.
 */
public class SRealTimePicturePnl extends JPanel
{
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
            return  notNullAndEmptyStr(msg.getSymbol());
        }
    }

    // 处理始查询symbol消息
    @Handler(filters = {@Filter(beginQuerySymbolFilter.class)})
    private void processBeginQuerySymbol(MBABeginQuerySymbol msg)
    {
        // 查询symbol的历史数据 （当前 或前一交易日的 5秒 历史数据）
        if (symbol != null && notNullAndEmptyStr(msg.getSymbol()))
        {
            //此处需要计算出要查询历史数据的时间,
           // symbol.reqHistoryDatas(msg.getSymbol(),);
        }

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
        // msg.symbolRealPrice

    }


}
