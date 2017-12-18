package com.dataModel;


import com.commdata.mbassadorObj.MBAtickPrice;
import com.commdata.pubdata.ContractRealTimeInfo;
import com.ib.client.Contract;
import com.ib.client.TickType;
import com.utils.TMbassadorSingleton;
import java.util.HashMap;
import java.util.Map;
import net.engio.mbassy.listener.Filter;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.IMessageFilter;
import net.engio.mbassy.subscription.SubscriptionContext;
import static com.utils.TConst.DATAMAAGER_BUS;
import static com.utils.TConst.REALTIMEPRICEMGR_BUS;

/**
 * 实时价格管理器器
 */
public class RealTimePriceMgr
{
    SDataManager dataManager;
    Symbol symbol;

    // reqid 与contract的map
    private static Map<Integer, Contract> reqid2ContractMap = new HashMap<>();
    // contract 的id与请求次数的map
    private static Map<Integer, Integer> contractid2ReqCountMap = new HashMap<>();
    // contract id 与实时价格的map
    private static Map<Integer, ContractRealTimeInfo> contractid2RealPriceMap = new HashMap<>();


    public RealTimePriceMgr(SDataManager dataManager, Symbol symbol)
    {
        this.dataManager = dataManager;
        this.symbol = symbol;

        // 订阅消息总线名称为 DATAMAAGER_BUS 的 消息
        TMbassadorSingleton.getInstance(DATAMAAGER_BUS).subscribe(this);
    }

    private boolean isValidMgr()
    {
        return dataManager != null && symbol != null;
    }

    private int getReqid(Contract contract)
    {
        if (contract != null)
        {
            for (Map.Entry<Integer, Contract> entry : reqid2ContractMap.entrySet())
            {
                if (entry.getValue().conid() == contract.conid())
                {
                    return entry.getKey();
                }
            }
        }
        return -1;
    }

    private void removeContract(Contract contract)
    {
        int reqid = getReqid(contract);
        if (reqid != -1)
        {
            contractid2ReqCountMap.remove(contract.conid());
            reqid2ContractMap.remove(reqid);
            contractid2RealPriceMap.remove(contract.conid());
        }
    }

    public void reqRealTimePrice(Contract contract)
    {
        if (contract != null && isValidMgr())
        {
            if (!contractid2ReqCountMap.containsKey(contract.conid()))
            {
                int reqid = symbol.reqOptionMktData(contract);
                reqid2ContractMap.put(reqid, contract);
                contractid2ReqCountMap.put(contract.conid(), 1); // contractid 计数 1次
                contractid2RealPriceMap.put(contract.conid(), new ContractRealTimeInfo(contract));
            }
            else
            {
                int reqCount = contractid2ReqCountMap.get(contract.conid());
                contractid2ReqCountMap.put(contract.conid(), reqCount + 1);
            }
        }
    }

    public void cancelRealTimePrice(Contract contract)
    {
        if (contract != null && isValidMgr())
        {
            Integer reqCount = contractid2ReqCountMap.get(contract.conid());
            if (reqCount != null)
            {
                if (reqCount == 1)
                {
                    int reqid = getReqid(contract);
                    if (reqid != -1)
                    {
                        symbol.cancelMktData(reqid);
                        removeContract(contract);
                    }
                }
                else if (reqCount > 1)
                {
                    contractid2ReqCountMap.put(contract.conid(), reqCount - 1);
                }
            }
        }
    }

    public void setRealTimePrice(int reqid, double realPrice)
    {
        if (reqid > 0)
        {
            if (reqid2ContractMap.containsKey(reqid))
            {
                Contract contract = reqid2ContractMap.get(reqid);
                if (contract != null && contractid2RealPriceMap.containsKey(contract.conid()))
                {
                    ContractRealTimeInfo contractRealTimeInfo = contractid2RealPriceMap.get(contract.conid());
                    if (contractRealTimeInfo != null)
                    {
                        contractRealTimeInfo.lastPrice = realPrice;
                    }
                }
            }
        }
    }

    public Double getRealTimePrice(Contract contract)
    {
        if (contract != null)
        {
            ContractRealTimeInfo contractRealTimeInfo = contractid2RealPriceMap.get(contract.conid());
            if (contractRealTimeInfo != null)
            {
                return contractRealTimeInfo.lastPrice;
            }
        }
        return null;
    }


    // 接收查询实时价格的消息过滤器
    public static class recvContractRealTimePriceFilter implements IMessageFilter<MBAtickPrice>
    {
        @Override
        public boolean accepts(MBAtickPrice msg, SubscriptionContext subscriptionContext)
        {
            return msg != null && reqid2ContractMap.containsKey(msg.tickerId);
        }
    }

    // 实时价格消息处理器
    @Handler(filters = {@Filter(recvContractRealTimePriceFilter.class)})
    private void getSymbolRealPrice(MBAtickPrice msg)
    {
        //   1 = 买价   2 = 卖价   4 = 最后价  6 = 最高价   7 = 最低价   9 = 收盘价   等等
        Contract contract = reqid2ContractMap.get(msg.tickerId);
        ContractRealTimeInfo contractRealTimeInfo =
                (contract != null) ? contractid2RealPriceMap.get(contract.conid()) : null;

        if (contractRealTimeInfo != null)
        {
            TickType tickType = TickType.get(msg.field);
            switch (tickType)
            {
                case ASK:
                    contractRealTimeInfo.salePrice = msg.price;
                    break;
                case BID:
                    contractRealTimeInfo.buyPrice = msg.price;
                    break;
                case LAST:
                    contractRealTimeInfo.lastPrice = msg.price;
                    break;
                case LOW:
                    contractRealTimeInfo.minLow = msg.price;
                    break;
                case HIGH:
                    contractRealTimeInfo.maxHigh = msg.price;
                    break;
                case OPEN:
                    contractRealTimeInfo.todayOpen = msg.price;
                    break;
                case CLOSE:
                    contractRealTimeInfo.yesterdayClose = msg.price;
                    break;
            }
            // 发布实时价格数据
            TMbassadorSingleton.getInstance(REALTIMEPRICEMGR_BUS).publish(new ContractRealTimeInfo(contractRealTimeInfo,
                                                                                                   tickType));
        }
    }


}
