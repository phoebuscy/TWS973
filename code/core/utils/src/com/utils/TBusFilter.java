package com.utils;

import net.engio.mbassy.listener.IMessageFilter;
import net.engio.mbassy.subscription.SubscriptionContext;

import java.math.BigInteger;

/**
 * Created by 123 on 2017/3/17.
 */
public class TBusFilter
{

    static public class StringFilter implements IMessageFilter<String>
    {
        public boolean accepts(String message, SubscriptionContext context)
        {
           // return message.startsWith("http");
            return TStringUtil.notNullAndEmptyStr(message);
        }
    }

    static public class BigIntegerFilter implements IMessageFilter<BigInteger>
    {
        public boolean accepts(BigInteger message, SubscriptionContext context)
        {
            return message != null;
        }
    }

    static public class ReturnObjFilter implements IMessageFilter<ReturnObj>
    {
        @Override
        public boolean accepts(ReturnObj returnObj, SubscriptionContext subscriptionContext)
        {
            return returnObj != null;
        }
    }


}
