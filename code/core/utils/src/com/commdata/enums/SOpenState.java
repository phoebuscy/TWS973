package com.commdata.enums;

/**
 * 开仓状态： 未开仓，正在开仓，已开仓，正在平仓
 */
public enum SOpenState
{
    NO_OPEN(1),  // 未开仓
    OPEN_ING(2), //  正在开仓
    OPENED(3),     //  已开仓
    CLOSE_ING(4); // 正在平仓


    private int val;
    private String label;

    SOpenState(int val)
    {
        this.val = val;
    }

    public boolean isNoOpen()
    {
        return val == NO_OPEN.val;
    }

    public boolean isOpening()
    {
        return val == OPEN_ING.val;
    }

    public boolean isOpened()
    {
        return  val == OPENED.val;
    }

    public boolean isClosing()
    {
        return val == CLOSE_ING.val;
    }

    @Override
    public String toString()
    {
        switch (this)
        {
            case NO_OPEN:
                return "NO_OPEN";
            case OPEN_ING:
                return "OPEN_ING";
            case OPENED:
                return "OPENED";
            case CLOSE_ING:
                return "CLOSE_ING";
        }
        return "--";
    }
}
