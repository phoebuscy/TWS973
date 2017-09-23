package com.dataModel;

import com.dataModel.mbassadorObj.MBASymbolRealPrice;
import com.dataModel.mbassadorObj.MBAtickPrice;
import com.ib.client.Contract;
import com.ib.client.EClientSocket;
import com.utils.TMbassadorSingleton;
import net.engio.mbassy.listener.Filter;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.IMessageFilter;
import net.engio.mbassy.subscription.SubscriptionContext;

import static com.dataModel.SDataManager.getReqId;
import static com.utils.TConst.DATAMAAGER_BUS;
import static com.utils.TConst.SYMBOL_BUS;
import static com.utils.TStringUtil.notNullAndEmptyStr;

/**
 * 对象类,如SPY
 */


public class Symbol
{

    private String symbolVal = "";                 // 对象名称
    private int querySymbolRealPriceTickid = -1;   // 查询symbol实时价格的tickid，用于接收实时数据和取消订阅之用
    private SDataManager dataManager;
    private double symbolRealPrice = 0.0;          // symbol的实时价格


    public Symbol(SDataManager dataManager)
    {
        this.dataManager = dataManager;

        // 订阅消息总线名称为 DATAMAAGER_BUS 的 消息
        TMbassadorSingleton.getInstance(DATAMAAGER_BUS).subscribe(this);
    }


    public Symbol(String symbolVal)
    {
        this.symbolVal = symbolVal;
    }

    public String getSymbolVal()
    {
        return symbolVal;
    }

    public void setSymbolVal(String symbleVal)
    {
        this.symbolVal = symbleVal;
    }

    public Double getSymbolRealPrice()
    {
        return symbolRealPrice;
    }

    public void querySymbolRealPrice()
    {
        EClientSocket m_client = dataManager.getM_client();
        if (m_client != null && notNullAndEmptyStr(symbolVal))
        {
            Contract contract = new Contract();
            contract.conid(0);
            contract.symbol(symbolVal);
            contract.secType("STK");
            contract.exchange("SMART");
            contract.primaryExch("ISLAND");
            contract.currency("USD");
            int tickID = getReqId();
            m_client.reqMktData(tickID, contract, "", false, false, null);
            querySymbolRealPriceTickid = tickID;
        }
    }

    public void cancelQuerySymbolRealPrice()
    {
        EClientSocket m_client = dataManager.getM_client();
        if (m_client != null)
        {
            m_client.cancelMktData(querySymbolRealPriceTickid);
        }
    }



    //------------------------------ 以下是处理动态返回的数据-------------------------------


    // 接收查询symbol的实时价格的消息过滤器
    static public class recvSymbolRealPriceFilter implements IMessageFilter<MBAtickPrice>
    {
        @Override
        public boolean accepts(MBAtickPrice msg, SubscriptionContext subscriptionContext)
        {
            return msg != null && msg.tickerId > 0;
        }
    }

    // 连接消息处理器
    @Handler(filters = {@Filter(recvSymbolRealPriceFilter.class)})
    private void getSymbolRealPrice(MBAtickPrice msg)
    {
        symbolRealPrice = msg.price;
        // Symbol发布数据
        TMbassadorSingleton.getInstance(SYMBOL_BUS).publish(new MBASymbolRealPrice(symbolRealPrice));

    }

}
