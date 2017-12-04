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
 * 历史数据仓库
 */

public class HistoricDataStorage
{
    private static Logger LogApp = LogManager.getLogger("applog");
    private static Logger LogMsg = LogManager.getLogger("datamsg");

    private int reqID;
    private List<MBAHistoricalData> historicalDataList = new ArrayList<>();

    // 锁
    private final Lock lock = new ReentrantLock();
    // 仓库空的条件变量
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

    // 生产num个产品
    public void produce(MBAHistoricalData historicalData)
    {
        // 获得锁
        lock.lock();
        if (historicalData != null)
        {
            historicalDataList.add(historicalData);
        }
        lock.unlock();
    }

    // 消费1个产品
    public void consume(List<MBAHistoricalData> historicalDataList)
    {
        // 获得锁
        lock.lock();
        try
        {
            // 由于条件不满足，消费阻塞
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
        // 释放锁
        lock.unlock();
    }


}
