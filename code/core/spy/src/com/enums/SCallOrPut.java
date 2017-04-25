package com.enums;

/**
 * Created by caiyong on 2016/12/26.
 */
public enum SCallOrPut
{
    NOTHING(-1),
    CALL(1),
    PUT(2);

    private int val;
    private String label;

    SCallOrPut(int val)
    {
        this.val = val;
    }

    @Override
    public String toString()
    {
        switch (this)
        {
            case NOTHING:
                return "--";
            case CALL:
                return "CALL";
            case PUT:
                return "PUT";
        }
        return "--";
    }
}
