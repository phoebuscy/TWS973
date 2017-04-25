package com;

/**
 * Created by caiyong on 2016/12/28.
 */
public class ReturnObj
{
    public Object returnObj = null;
    public boolean success = false;

    public ReturnObj(Object returnObj, boolean success)
    {
        this.returnObj = returnObj;
        this.success = success;
    }
    public ReturnObj()
    {
    }

    public void setData(Object obj, boolean success)
    {
        this.returnObj = obj;
        this.success = success;
    }

    public void setReturnObj(Object obj)
    {
        this.returnObj = obj;
    }
    public void setSuccess(boolean success)
    {
        this.success = success;
    }
}
