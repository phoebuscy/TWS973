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
        lock.lock();
        getDatafinished.signalAll();
        lock.unlock();
    }

    // ����num����Ʒ
    public void produce(MBAHistoricalData historicalData)
    {
        if (historicalData != null)
        {
            historicalDataList.add(historicalData);
        }
    }

    // ����1����Ʒ
    public List<MBAHistoricalData> consume()
    {
        // �����
        lock.lock();
        try
        {
            // �������������㣬��������
            getDatafinished.await(1,TimeUnit.MINUTES);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        lock.unlock();
        return this.historicalDataList;
    }


}
