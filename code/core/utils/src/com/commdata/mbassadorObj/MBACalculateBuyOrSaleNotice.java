package com.commdata.mbassadorObj;

import com.ib.client.Types;

public class MBACalculateBuyOrSaleNotice
{
    public Types.Action action;
    public Types.Right right;

    public MBACalculateBuyOrSaleNotice(Types.Action action, Types.Right right)
    {
        this.action = action;
        this.right = right;
    }

}
