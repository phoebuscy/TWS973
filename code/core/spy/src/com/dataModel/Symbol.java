package com.dataModel;

import com.answermodel.AnswerObj;
import com.dataModel.mbassadorObj.MBAOptionExpireDayList;
import com.dataModel.mbassadorObj.MBASymbolRealPrice;
import com.dataModel.mbassadorObj.MBAtickPrice;
import com.ib.client.Contract;
import com.ib.client.ContractDetails;
import com.ib.client.EClientSocket;
import com.ib.client.TagValue;
import com.ib.client.TickType;
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
import static com.utils.TStringUtil.nullOrEmptyStr;

/**
 * 对象类,如SPY
 */


public class Symbol
{

    private String symbolVal = "";                 // 对象名称
    private static int querySymbolRealPriceTickid = -1;   // 查询symbol实时价格的tickid，用于接收实时数据和取消订阅之用
    private SDataManager dataManager;
    private double symbolRealPrice = 0D;          // symbol的实时价格
    private double symbolTodayOpenPrice = 0D;   // symbol 今开价格
    private double symbolYesterdayClosePrice = 0D;  // symbol 昨收价格
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

    public Double getSymbolYesterdayClosePrice()
    {
        return symbolYesterdayClosePrice;
    }

    public Double getSymbolTodayOpenPrice()
    {
        return symbolTodayOpenPrice;
    }

    public int reqOptionMktData(Contract contract)
    {
        EClientSocket m_client = dataManager.getM_client();
        if (m_client != null && contract != null)
        {
            int reqId = getReqId();
            String genericTickList = "";
            boolean snapshot = false;
            boolean regulatorySnaphsot = false;
            m_client.reqMktData(reqId,
                                contract,
                                genericTickList,
                                snapshot,
                                regulatorySnaphsot,
                                Collections.emptyList());
            return reqId;
        }
        return -1;
    }

    public void cancelMktData(int reqId)
    {
        EClientSocket m_client = dataManager.getM_client();
        if (m_client != null && reqId > 0)
        {
            m_client.cancelMktData(reqId);
        }
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

    /**
     * 获取历史数据 （当单位选 秒 时，最小时间bar为5秒）
     * 例如：查询 spy option  的历史数据 ，其 Contract 如下(即查询回来的Contract)
     * Contract optCtr = new Contract();
     optCtr.conid(289715299);
     optCtr.symbol("SPY");
     optCtr.secType(Types.SecType.OPT);
     optCtr.lastTradeDateOrContractMonth("20171006");
     optCtr.strike(253.5);
     optCtr.right(Types.Right.Call);
     optCtr.multiplier("100");
     optCtr.exchange("SMART");
     optCtr.currency("USD");
     optCtr.localSymbol("SPY  171006C00253500");
     optCtr.tradingClass("SPY");
     optCtr.includeExpired(false);
     *  注意： m_client.reqHistoricalData 的参数中
     *  endDateTime 格式为：20171006 23:59:00
     *  durationStr 格式为：10000 S
     *  barSize  格式为：10 secs
     *  whatToShow 格式为：TRADES
     *  useRTH  格式为： rthOnly ? 1 : 0
     *  formatDate 格式为：2
     *  chartOptions 格式为： Collections.emptyList()
     *
     * @param symbol
     * @param endDataTime
     * @param durationStr
     * @param barSize
     */
    public void reqHistoryDatas(String symbol, String endDataTime, String durationStr, String barSize)
    {
        EClientSocket m_client = dataManager.getM_client();
        if (m_client != null && notNullAndEmptyStr(symbolVal))
        {
            String t_symbol = nullOrEmptyStr(symbol) ? "SPY" : symbol;
            String t_endDataTime = nullOrEmptyStr(endDataTime) ? "20170726 12:00:00" : endDataTime;
            String t_durationStr = nullOrEmptyStr(durationStr) ? "1 D" : durationStr;
            String t_barSize = nullOrEmptyStr(barSize) ? "1 minute" : barSize;

            Contract contract = new Contract();
            contract.conid(0);
            contract.symbol(t_symbol);
            contract.secType("STK");
            contract.exchange("SMART");
            contract.primaryExch("ISLAND");
            contract.currency("USD");

            String whatToShow = "TRADES";
            int useRTH = 0;
            int formatData = 2;
            List<TagValue> tagValueList = new ArrayList<>();

            m_client.reqHistoricalData(getReqId(),
                                       contract,
                                       t_endDataTime,
                                       t_durationStr,
                                       t_barSize,
                                       whatToShow,
                                       useRTH,
                                       formatData,
                                       tagValueList);
        }
    }


    //------------------------------ 以下是处理动态返回的数据-------------------------------


    // 接收查询symbol的实时价格的消息过滤器
    static public class recvSymbolRealPriceFilter implements IMessageFilter<MBAtickPrice>
    {
        @Override
        public boolean accepts(MBAtickPrice msg, SubscriptionContext subscriptionContext)
        {
            return msg.tickerId == querySymbolRealPriceTickid;
        }
    }

    // 连接消息处理器
    @Handler(filters = {@Filter(recvSymbolRealPriceFilter.class)})
    private void getSymbolRealPrice(MBAtickPrice msg)
    {
        //   1 = 买价   2 = 卖价   4 = 最后价  6 = 最高价   7 = 最低价      9 = 收盘价
        if( msg.field == TickType.LAST.index())
        {
            symbolRealPrice = msg.price;
            // Symbol发布数据
            TMbassadorSingleton.getInstance(SYMBOL_BUS).publish(new MBASymbolRealPrice(symbolRealPrice));
        }
        else if(msg.field == TickType.OPEN.index())
        {
            symbolTodayOpenPrice = msg.price;
        }
        else if(msg.field == TickType.CLOSE.index())
        {
            symbolYesterdayClosePrice = msg.price;
        }
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
        }
        else
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
                }
                else
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
