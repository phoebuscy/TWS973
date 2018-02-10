package com.commdata.enums;

/**
 * ����״̬�� δ���֣����ڿ��֣��ѿ��֣�����ƽ��
 */
public enum SOpenState
{
    NO_OPEN(1),  // δ����
    OPEN_ING(2), //  ���ڿ���
    OPENED(3),     //  �ѿ���
    CLOSE_ING(4); // ����ƽ��


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
