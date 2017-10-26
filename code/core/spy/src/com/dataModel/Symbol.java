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
 * ������,��SPY
 */


public class Symbol
{

    private String symbolVal = "";                 // ��������
    private static int querySymbolRealPriceTickid = -1;   // ��ѯsymbolʵʱ�۸��tickid�����ڽ���ʵʱ���ݺ�ȡ������֮��
    private SDataManager dataManager;
    private double symbolRealPrice = 0D;          // symbol��ʵʱ�۸�
    private double symbolTodayOpenPrice = 0D;   // symbol �񿪼۸�
    private double symbolYesterdayClosePrice = 0D;  // symbol ���ռ۸�
    private static int queryOptionChainReqId = -1; // ��ѯ��Ȩ����reqid
    private List<ContractDetails> contractDetailsList = new ArrayList<>();
    private Map<String, List<ContractDetails>> day2CtrdMap = new HashMap<>();
    private List<String> optionExpireDayLst = new ArrayList<>();   // �������Ȩ��������


    public Symbol(SDataManager dataManager)
    {
        this.dataManager = dataManager;

        // ������Ϣ��������Ϊ DATAMAAGER_BUS �� ��Ϣ
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

    // ��ѯ��Ȩ��
    public void queryOptionChain()
    {
        if (notNullAndEmptyStr(symbolVal))
        {
            Contract contract = new Contract();
            contract.conid(0);
            contract.symbol(symbolVal);
            contract.secType("OPT");
            contract.lastTradeDateOrContractMonth(getSysYear());  // ��ȡ��ǰ��� �磺2017
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
        double curSymbolRealPrice = getSymbolRealPrice();  // ��Ҫ��һ��������ȡ��ǰsymbol�ļ۸�
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
     * ��ȡ��ʷ���� ������λѡ �� ʱ����Сʱ��barΪ5�룩
     * ���磺��ѯ spy option  ����ʷ���� ���� Contract ����(����ѯ������Contract)
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
     *  ע�⣺ m_client.reqHistoricalData �Ĳ�����
     *  endDateTime ��ʽΪ��20171006 23:59:00
     *  durationStr ��ʽΪ��10000 S
     *  barSize  ��ʽΪ��10 secs
     *  whatToShow ��ʽΪ��TRADES
     *  useRTH  ��ʽΪ�� rthOnly ? 1 : 0
     *  formatDate ��ʽΪ��2
     *  chartOptions ��ʽΪ�� Collections.emptyList()
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


    //------------------------------ �����Ǵ���̬���ص�����-------------------------------


    // ���ղ�ѯsymbol��ʵʱ�۸����Ϣ������
    static public class recvSymbolRealPriceFilter implements IMessageFilter<MBAtickPrice>
    {
        @Override
        public boolean accepts(MBAtickPrice msg, SubscriptionContext subscriptionContext)
        {
            return msg.tickerId == querySymbolRealPriceTickid;
        }
    }

    // ������Ϣ������
    @Handler(filters = {@Filter(recvSymbolRealPriceFilter.class)})
    private void getSymbolRealPrice(MBAtickPrice msg)
    {
        //   1 = ���   2 = ����   4 = ����  6 = ��߼�   7 = ��ͼ�      9 = ���̼�
        if( msg.field == TickType.LAST.index())
        {
            symbolRealPrice = msg.price;
            // Symbol��������
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


    // ������Ȩ����Ϣ������
    static public class optionChainFilter implements IMessageFilter<AnswerObj>
    {
        @Override
        public boolean accepts(AnswerObj msg, SubscriptionContext subscriptionContext)
        {
            return msg.getReqid() == queryOptionChainReqId;
        }
    }

    // ������Ȩ����Ϣ������
    @Handler(filters = {@Filter(optionChainFilter.class)})
    private void getOptionChain(AnswerObj msg)
    {
        Object obj = msg.getAnswerObj();
        if (obj instanceof ContractDetails)
        {
            contractDetailsList.add((ContractDetails) obj);
        }
    }

    // ���ղ�ѯcontractDetail��ϵĹ�����
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
            // Symbol��������
            TMbassadorSingleton.getInstance(SYMBOL_BUS).publish(new MBAOptionExpireDayList(optionExpireDayLst));
        }
        int a = 1;
    }


}
