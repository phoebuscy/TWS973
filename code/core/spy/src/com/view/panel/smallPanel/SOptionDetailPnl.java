package com.view.panel.smallPanel;

import com.commdata.mbassadorObj.MBAReqIDContractDetails;
import com.commdata.mbassadorObj.MBAtickPrice;
import com.ib.client.Contract;
import com.ib.client.TickType;
import com.ib.client.Types;
import com.model.SOptionRealTimeInfoModel;
import com.utils.GBC;
import com.utils.TMbassadorSingleton;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JPanel;
import net.engio.mbassy.listener.Filter;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.IMessageFilter;
import net.engio.mbassy.subscription.SubscriptionContext;
import static com.utils.SUtil.getDimension;
import static com.utils.TConst.DATAMAAGER_BUS;
import static com.utils.TConst.SYMBOL_BUS;

/**
 * Created by caiyong on 2016/12/24.
 */
public class SOptionDetailPnl extends JPanel
{

    private Component parentWin;
    private Dimension parentDimension;
    private SOptionRealTimeInfoPnl callInfoPnl = new SOptionRealTimeInfoPnl(this, Types.Right.Call);
    private SOptionRealTimeInfoPnl putInfoPnl = new SOptionRealTimeInfoPnl(this, Types.Right.Put);

    // 查询买卖价的市场数据reqid和contract的map
    private static Map<Integer, MBAReqIDContractDetails> topMktDataReqID2ContractsMap = new HashMap<>();

    public SOptionDetailPnl(Component parentWin)
    {
        setBackground(Color.gray);
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
        setPreferredSize(getDimension(parentDimension, 0.5, 0.4));
    }


    private void buildGUI()
    {
        setLayout(new GridBagLayout());
        add(callInfoPnl, new GBC(0, 0).setAnchor(GBC.WEST).setIpad(50, 5).setWeight(10, 10).setFill(GBC.BOTH));
        add(putInfoPnl, new GBC(1, 0).setAnchor(GBC.EAST).setIpad(50, 5).setWeight(10, 10).setFill(GBC.BOTH));
    }

    // 接收双击期权实时信息table的行信息过滤方法
    static public class recvOptRealTimePriceTableDoubleClickInfo implements IMessageFilter<MBAReqIDContractDetails>
    {
        @Override
        public boolean accepts(MBAReqIDContractDetails msg, SubscriptionContext subscriptionContext)
        {
            return msg != null;
        }
    }

    // 处理双击期权表得到的期权信息
    @Handler(filters = {@Filter(recvOptRealTimePriceTableDoubleClickInfo.class)})
    private void processDoubleClickOptTableInfo(MBAReqIDContractDetails msg)
    {
        BigInteger reqid = BigInteger.valueOf(msg.reqid);
        topMktDataReqID2ContractsMap.put(reqid.intValue(), msg);
        SOptionRealTimeInfoModel infoModel = getOptRealTimeInfoModel(msg);
        if (Types.Right.Call.equals(infoModel.getRight()))
        {
            callInfoPnl.setData(infoModel);
        }
        else
        {
            putInfoPnl.setData(infoModel);
        }
    }


    // 接收查询symbol的实时价格的消息过滤器
    static public class recvOptionMktDataFilter implements IMessageFilter<MBAtickPrice>
    {
        @Override
        public boolean accepts(MBAtickPrice msg, SubscriptionContext subscriptionContext)
        {
            return topMktDataReqID2ContractsMap.containsKey(msg.tickerId);
        }
    }

    // 实时消息处理器
    @Handler(filters = {@Filter(recvOptionMktDataFilter.class)})
    private void processOptionMktData(MBAtickPrice msg)
    {
        MBAReqIDContractDetails reqIDContractDetails = topMktDataReqID2ContractsMap.get(msg.tickerId);
        Contract contract = reqIDContractDetails.contractDetails.contract();
        if (contract != null)
        {
            Types.Right right = contract.right();
            SOptionRealTimeInfoModel infoModel = getOptRealTimeInfoModel(right, msg);
            if (Types.Right.Call.equals(right))
            {
                callInfoPnl.setData(infoModel);
            }
            else
            {
                putInfoPnl.setData(infoModel);
            }
        }
    }

    private SOptionRealTimeInfoModel getOptRealTimeInfoModel(MBAReqIDContractDetails msg)
    {
        SOptionRealTimeInfoModel infoModel = new SOptionRealTimeInfoModel();
        if (msg != null && msg.contractDetails != null)
        {
            Contract contract = msg.contractDetails.contract();
            infoModel.setRight(contract.right());
            infoModel.setObj(contract.symbol());
            infoModel.setRealTimePrice(Double.toString(msg.currentPrice));
            infoModel.setYestadayClosePrice(Double.toString(msg.yesterdayClose));
            infoModel.setTodayOpenPrice(Double.toString(msg.todayOpen));
            infoModel.setOperatePrice(Double.toString(contract.strike()));
            infoModel.setExpireDate(contract.lastTradeDateOrContractMonth());
        }
        return infoModel;
    }

    private SOptionRealTimeInfoModel getOptRealTimeInfoModel(Types.Right right, MBAtickPrice msg)
    {
        SOptionRealTimeInfoModel infoModel = new SOptionRealTimeInfoModel();
        infoModel.setRight(right);
        TickType tickType = TickType.get(msg.field);
        switch (tickType)
        {
            case BID:
            case DELAYED_BID:
                infoModel.setCurBuyPrice(Double.toString(msg.price));
                break;
            case ASK:
            case DELAYED_ASK:
                infoModel.setCurSellPrice(Double.toString(msg.price));
                break;
            case LAST:
            case DELAYED_LAST:
                infoModel.setRealTimePrice(Double.toString(msg.price));
                break;
            case OPEN:
            case DELAYED_OPEN:
                infoModel.setTodayOpenPrice(Double.toString(msg.price));
                break;
            case CLOSE:
            case DELAYED_CLOSE:
                infoModel.setYestadayClosePrice(Double.toString(msg.price));
                break;
        }
        return infoModel;
    }

}
