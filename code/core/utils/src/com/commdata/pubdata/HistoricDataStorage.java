package com.commdata.pubdata;

import com.commdata.mbassadorObj.MBAHistoricalData;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ��ʷ���ݲֿ�
 */

public class HistoricDataStorage
{
    private static Logger LogApp = LogManager.getLogger("applog");
    private static Logger LogMsg = LogManager.getLogger("datamsg");

    private int reqID;
    private List<MBAHistoricalData> historicalDataList = new ArrayList<>();

    // ��
    private final Lock lock = new ReentrantLock();
    // �ֿ�յ���������
    private final Condition getDatafinished = lock.newCondition();

    public HistoricDataStorage(int reqID)
    {
        this.reqID = reqID;
    }

    public void setGetDatafinished()
    {
        if(getDatafinished != null)
        {
            getDatafinished.signal();
        }
    }

    // ����num����Ʒ
    public void produce(MBAHistoricalData historicalData)
    {
        // �����
        lock.lock();
        if (historicalData != null)
        {
            historicalDataList.add(historicalData);
        }
        lock.unlock();
    }

    // ����1����Ʒ
    public void consume(List<MBAHistoricalData> historicalDataList)
    {
        // �����
        lock.lock();
        try
        {
            // �������������㣬��������
            getDatafinished.await(30, TimeUnit.SECONDS);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        if(historicalDataList != null)
        {
            historicalDataList.clear();
            historicalDataList.addAll(this.historicalDataList);
        }
        // �ͷ���
        lock.unlock();
    }


}
