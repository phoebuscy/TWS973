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
 * ʵʱ�۸��������
 */
public class RealTimePriceMgr
{
    SDataManager dataManager;
    Symbol symbol;

    // reqid ��contract��map
    private static Map<Integer, Contract> reqid2ContractMap = new HashMap<>();
    // contract ��id�����������map
    private static Map<Integer, Integer> contractid2ReqCountMap = new HashMap<>();
    // contract id ��ʵʱ�۸��map
    private static Map<Integer, ContractRealTimeInfo> contractid2RealPriceMap = new HashMap<>();


    public RealTimePriceMgr(SDataManager dataManager, Symbol symbol)
    {
        this.dataManager = dataManager;
        this.symbol = symbol;

        // ������Ϣ��������Ϊ DATAMAAGER_BUS �� ��Ϣ
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
                contractid2ReqCountMap.put(contract.conid(), 1); // contractid ���� 1��
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


    // ���ղ�ѯʵʱ�۸����Ϣ������
    public static class recvContractRealTimePriceFilter implements IMessageFilter<MBAtickPrice>
    {
        @Override
        public boolean accepts(MBAtickPrice msg, SubscriptionContext subscriptionContext)
        {
            return msg != null && reqid2ContractMap.containsKey(msg.tickerId);
        }
    }

    // ʵʱ�۸���Ϣ������
    @Handler(filters = {@Filter(recvContractRealTimePriceFilter.class)})
    private void getSymbolRealPrice(MBAtickPrice msg)
    {
        //   1 = ���   2 = ����   4 = ����  6 = ��߼�   7 = ��ͼ�   9 = ���̼�   �ȵ�
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
            // ����ʵʱ�۸�����
            TMbassadorSingleton.getInstance(REALTIMEPRICEMGR_BUS).publish(new ContractRealTimeInfo(contractRealTimeInfo,
                                                                                                   tickType));
        }
    }


}
