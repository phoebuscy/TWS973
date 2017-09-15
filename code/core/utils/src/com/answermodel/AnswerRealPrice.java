package com.answermodel;

public class AnswerRealPrice
{

    public int reqid = -1;
    public Object answerObj = null;

    public AnswerRealPrice(int reqid, Object answerObj)
    {
        this.reqid = reqid;
        this.answerObj = answerObj;
    }

    public int getReqid()
    {
        return reqid;
    }

    public Object getAnswerObj()
    {
        return answerObj;
    }


}
