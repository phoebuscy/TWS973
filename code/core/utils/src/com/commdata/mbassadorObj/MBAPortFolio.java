package com.commdata.mbassadorObj;


import com.ib.client.Contract;

/**
 * ������������Ѿ�����EClientSocket�����reqAccountUpdates()����ʱ���á�
 * ����	����
 * contract	�ýṹ�������׺�Լ�������� ��Լ�Ľ���������û��ΪͶ����ϸ��¶����á�
 * position	��������ʾ��Լͷ�硣 ���ͷ��Ϊ0����ʾͷ��ձ�ƽ�֡�
 * marketPrice	��Ʒ�ĵ�λ�۸�
 * marketValue	��Ʒ������ֵ��
 * averageCost	ÿ�ɵ�ƽ���ɱ��ļ���������ͷ�������������ĳɱ���ִ�м۸�Ӷ�𣩡�
 * unrealizedPNL	��δƽ��ͷ��ĵ�ǰ��ֵ��ƽ���ɱ���ƽ���ɱ�ֵ�Ĳ
 * realizedPNL	��ʾƽ��ͷ�������Ϊ��Ľ���ִ�гɱ���ִ�м۸񣫽���Ӷ�𣩺�ƽ��ִ�гɱ���ִ�м۸�ƽ��ͷ��Ӷ�𣩵Ĳ
 * accountName	��ϢӦ���ڵ��˻����ơ������ڽ��ڹ������˻���Ϣ��
 */

public class MBAPortFolio
{
    public Contract contract;
    public double position;
    public double marketPrice;
    public double marketValue;
    public double averageCost;
    public double unrealizedPNL;
    public double realizedPNL;
    public String accountName;

    public MBAPortFolio(Contract contract,
                        double position,
                        double marketPrice,
                        double marketValue,
                        double averageCost,
                        double unrealizedPNL,
                        double realizedPNL,
                        String accountName)
    {
        this.contract = contract;
        this.position = position;
        this.marketPrice = marketPrice;
        this.marketValue = marketValue;
        this.averageCost = averageCost;
        this.unrealizedPNL = unrealizedPNL;
        this.realizedPNL = realizedPNL;
        this.accountName = accountName;
    }


}
