package com.commdata.mbassadorObj;


/*
key
��ʾһ���˻�ֵ���͵��ַ����� �кܶ�ɱ����͵Ŀ��õı�ǩ��������г�����������

CashBalance - �˻��ֽ����
DayTradesRemaining - ʣ�ཻ����
EquityWithLoanValue - �����ֵ��Ȩ
InitMarginReq - ��ǰ��ʼ��֤��Ҫ��
MaintMarginReq - ��ǰά�ֱ�֤��
NetLiquidation - ������ֵ
value	���ǩ��ص�ֵ��
currency	��ֵΪ�������͵�����£�����������͡�
account	˵����ϢӦ�õ��˻��������ڽ��ڹ������˻���Ϣ��
 */

public class MBAAccountValue
{
    public String key;
    public String value;
    public String currency;
    public String accountName;

    public MBAAccountValue(String key, String value, String currency, String accountName)
    {
        this.key = key;
        this.value = value;
        this.currency = currency;
        this.accountName = accountName;
    }


}
