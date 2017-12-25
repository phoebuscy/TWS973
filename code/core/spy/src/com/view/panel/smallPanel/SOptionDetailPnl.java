package com.view.panel.smallPanel;

import com.commdata.mbassadorObj.MBAReqIDContractDetails;
import com.commdata.pubdata.ContractRealTimeInfo;
import com.dataModel.SDataManager;
import com.dataModel.Symbol;
import com.ib.client.Contract;
import com.ib.client.Types;
import com.model.SOptionRealTimeInfoModel;
import com.utils.GBC;
import com.utils.TMbassadorSingleton;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import javax.swing.JPanel;
import net.engio.mbassy.listener.Filter;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.IMessageFilter;
import net.engio.mbassy.subscription.SubscriptionContext;
import static com.utils.SUtil.getDimension;
import static com.utils.TConst.DATAMAAGER_BUS;
import static com.utils.TConst.REALTIMEPRICEMGR_BUS;
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
    private Symbol symbol = SDataManager.getInstance().getSymbol();
    private static Contract callContract;
    private static Contract putContract;


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
        TMbassadorSingleton.getInstance(REALTIMEPRICEMGR_BUS).subscribe(this);
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
        Contract contract = msg.contract;
        ContractRealTimeInfo ctrRealtimeInfo = symbol.getContractRealTimeInfo(contract);
        SOptionRealTimeInfoModel infoModel = getOptRealTimeInfoModel(ctrRealtimeInfo);
        if (Types.Right.Call.equals(contract.right()))
        {
            symbol.cancelRealTimePrice(callContract);
            callContract = contract;
            symbol.reqRealTimePrice(contract);
            callInfoPnl.setData(infoModel);
        }
        else
        {
            symbol.cancelRealTimePrice(putContract);
            putContract = contract;
            symbol.reqRealTimePrice(contract);
            putInfoPnl.setData(infoModel);
        }
    }

    // symbolContract 实时价格过滤器
    public static class rcvContractRealTimePriceFilter implements IMessageFilter<ContractRealTimeInfo>
    {
        @Override
        public boolean accepts(ContractRealTimeInfo msg, SubscriptionContext subscriptionContext)
        {
            if (msg != null && msg.contract != null)
            {
                return (callContract != null && callContract.conid() == msg.contract.conid()) ||
                       (putContract != null && putContract.conid() == msg.contract.conid());

            }
            return false;
        }
    }

    @Handler(filters = {@Filter(rcvContractRealTimePriceFilter.class)})
    private void processContractRealTimePrice(ContractRealTimeInfo msg)
    {
        if (msg != null)
        {
            Contract contract = msg.contract;
            SOptionRealTimeInfoModel infoModel = getOptRealTimeInfoModel(msg);
            if(Types.Right.Call.equals(contract.right()))
            {
                callInfoPnl.setData(infoModel);
            }
            else
            {
                putInfoPnl.setData(infoModel);
            }
        }
    }

    private SOptionRealTimeInfoModel getOptRealTimeInfoModel(ContractRealTimeInfo msg)
    {
        SOptionRealTimeInfoModel infoModel = new SOptionRealTimeInfoModel();
        if (msg != null)
        {
            Contract contract = msg.contract;
            infoModel.setRight(contract.right());
            infoModel.setObj(contract.symbol());
            infoModel.setRealTimePrice(String.valueOf(msg.lastPrice));
            infoModel.setYestadayClosePrice(String.valueOf(msg.yesterdayClose));
            infoModel.setTodayOpenPrice(String.valueOf(msg.todayOpen));
            infoModel.setCurBuyPrice(String.valueOf(msg.buyPrice));
            infoModel.setCurSalePrice(String.valueOf(msg.salePrice));
            infoModel.setTodayMaxPrice(String.valueOf(msg.maxHigh));
            infoModel.setTodayMinPrice(String.valueOf(msg.minLow));
            infoModel.setOperatePrice(Double.toString(contract.strike()));
            infoModel.setExpireDate(contract.lastTradeDateOrContractMonth());
        }
        return infoModel;
    }


}
