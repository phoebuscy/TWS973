package com.commdata.mbassadorObj;


/*
key
表示一种账户值类型的字符串。 有很多可被发送的可用的标签，这里仅列出几个样本：

CashBalance - 账户现金余额
DayTradesRemaining - 剩余交易日
EquityWithLoanValue - 含借贷值股权
InitMarginReq - 当前初始保证金要求
MaintMarginReq - 当前维持保证金
NetLiquidation - 净清算值
value	与标签相关的值。
currency	在值为货币类型的情况下，定义货币类型。
account	说明信息应用的账户。适用于金融顾问子账户信息。
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
