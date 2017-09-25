package com.dataModel;

import com.answermodel.AnswerObj;
import com.dataModel.mbassadorObj.MBAOptionExpireDayList;
import com.dataModel.mbassadorObj.MBASymbolRealPrice;
import com.dataModel.mbassadorObj.MBAtickPrice;
import com.ib.client.Contract;
import com.ib.client.ContractDetails;
import com.ib.client.EClientSocket;
import com.utils.TMbassadorSingleton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.engio.mbassy.listener.Filter;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.IMessageFilter;
import net.engio.mbassy.subscription.SubscriptionContext;
import static com.dataModel.SDataManager.getReqId;
import static com.utils.SUtil.getSysYear;
import static com.utils.TConst.AK_CONTRACT_DETAIL_END;
import static com.utils.TConst.DATAMAAGER_BUS;
import static com.utils.TConst.SYMBOL_BUS;
import static com.utils.TPubUtil.getAKmsg;
import static com.utils.TPubUtil.notNullAndEmptyCollection;
import static com.utils.TPubUtil.notNullAndEmptyMap;
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
    private static int queryOptionChainReqId = -1; // 查询期权链的reqid
    private List<ContractDetails> contractDetailsList = new ArrayList<>();
    private Map<String, List<ContractDetails>> day2CtrdMap = new HashMap<>();
    private List<String> optionExpireDayLst = new ArrayList<>();   // 排序的期权结束日期



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

    // 查询期权链
    public void queryOptionChain()
    {
        if (notNullAndEmptyStr(symbolVal))
        {
            Contract contract = new Contract();
            contract.conid(0);
            contract.symbol(symbolVal);
            contract.secType("OPT");
            contract.lastTradeDateOrContractMonth(getSysYear());  // 获取当前年份 如：2017
            contract.strike(0.0);
            contract.exchange("SMART");
            contract.currency("USD");

            queryOptionChainReqId = getReqId();
            EClientSocket m_client = dataManager.getM_client();
            if (m_client != null)
            {
                m_client.reqContractDetails(queryOptionChainReqId, contract);
            }
        }
    }


    public Map<Double, List<ContractDetails>> getStrike2ContractDtalsLst(String expireDay)
    {
        Map<Double, List<ContractDetails>> strike2ContractDtalsLst = new HashMap<>();
        double curSymbolRealPrice = getSymbolRealPrice();  // 需要用一个方法获取当前symbol的价格
        List<ContractDetails> ctrdetailLst = day2CtrdMap.get(expireDay);

        if (notNullAndEmptyCollection(ctrdetailLst))
        {
            for (ContractDetails ctrDtails : ctrdetailLst)
            {
                Double strike = ctrDtails.contract().strike();
                if (Double.compare(Math.abs(curSymbolRealPrice - strike), 3.0) == -1)
                {
                    List<ContractDetails> contractDetailsLst = strike2ContractDtalsLst.get(strike);
                    if (contractDetailsLst == null)
                    {
                        contractDetailsLst = new ArrayList<>();
                        strike2ContractDtalsLst.put(strike, contractDetailsLst);
                    }
                    contractDetailsLst.add(ctrDtails);
                }
            }
        }
        return strike2ContractDtalsLst;
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


    // 接收期权链消息过滤器
    static public class optionChainFilter implements IMessageFilter<AnswerObj>
    {
        @Override
        public boolean accepts(AnswerObj msg, SubscriptionContext subscriptionContext)
        {
            return msg.getReqid() == queryOptionChainReqId;
        }
    }

    // 接收期权链消息处理器
    @Handler(filters = {@Filter(optionChainFilter.class)})
    private void getOptionChain(AnswerObj msg)
    {
        Object obj = msg.getAnswerObj();
        if (obj instanceof ContractDetails)
        {
            contractDetailsList.add((ContractDetails) obj);
        }
    }

    // 接收查询contractDetail完毕的过滤器
    static public class contractDetailEndFilter implements IMessageFilter<String>
    {
        @Override
        public boolean accepts(String msg, SubscriptionContext subscriptionContext)
        {
            if (msg.startsWith(AK_CONTRACT_DETAIL_END))
            {
                return String.valueOf(queryOptionChainReqId).equals(getAKmsg(AK_CONTRACT_DETAIL_END, msg));
            }
            return false;
        }
    }

    @Handler(filters = {@Filter(contractDetailEndFilter.class)})
    private void getContractDetailend(String msg)
    {
        if (day2CtrdMap == null)
        {
            day2CtrdMap = new HashMap<>();
        } else
        {
            day2CtrdMap.clear();
        }

        if (notNullAndEmptyCollection(contractDetailsList))
        {
            for (ContractDetails ctrd : contractDetailsList)
            {
                String lastDay = ctrd.contract().lastTradeDateOrContractMonth();
                if (day2CtrdMap.containsKey(lastDay))
                {
                    day2CtrdMap.get(lastDay).add(ctrd);
                } else
                {
                    List<ContractDetails> ctrLst = new ArrayList<>();
                    ctrLst.add(ctrd);
                    day2CtrdMap.put(lastDay, ctrLst);
                }
            }
        }
        if (notNullAndEmptyMap(day2CtrdMap))
        {
            optionExpireDayLst = new ArrayList<>(day2CtrdMap.keySet());
            Collections.sort(optionExpireDayLst);
            // Symbol发布数据
            TMbassadorSingleton.getInstance(SYMBOL_BUS).publish(new MBAOptionExpireDayList(optionExpireDayLst));
        }
        int a = 1;
    }



}
