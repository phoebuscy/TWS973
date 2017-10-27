package com.view.panel;

import com.dataModel.mbassadorObj.MBASymbolRealPrice;
import com.utils.GBC;
import com.utils.SUtil;
import com.utils.TMbassadorSingleton;
import com.view.panel.smallPanel.SRealTimePnl;

import com.view.panel.smallPanel.SSymbolePanel;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import net.engio.mbassy.listener.Filter;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.IMessageFilter;
import net.engio.mbassy.subscription.SubscriptionContext;
import org.jfree.data.Range;
import static com.utils.TConst.SYMBOL_BUS;

/**
 * Created by 123 on 2016/12/18.
 */
public class SRealTimePicturePnl extends JPanel
{
    private Component parentWin;
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
        this.parentWin = parentWin;
        parentDimension = parentWin.getSize();
        setDimension();

        // 订阅消息总线名称为 DATAMAAGER_BUS 的 消息
        TMbassadorSingleton.getInstance(SYMBOL_BUS).subscribe(this);
    }

    private void setDimension()
    {
        setSize(SUtil.getDimension(parentDimension, 0.5, 1.0));
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
    @Handler(filters = {@Filter(SSymbolePanel.realPriceStatusFilter.class)})
    private void getRealPrice(MBASymbolRealPrice msg)
    {
        Range yRange = sSpyRealTimePnl.getYRange();

        Double lower = yRange.getLowerBound();
        Double upper = yRange.getUpperBound();

        if (yRange == null || msg.symbolRealPrice < lower || (msg.symbolRealPrice - lower) > 0.5 ||
            msg.symbolRealPrice > upper || (upper - msg.symbolRealPrice) > 1.5)
        {
            sSpyRealTimePnl.setYRange(msg.symbolRealPrice - 1.0, msg.symbolRealPrice + 1.0);
        }
        Date date = new Date();
        sSpyRealTimePnl.addValue(date, msg.symbolRealPrice);
        // msg.symbolRealPrice

    }


}
