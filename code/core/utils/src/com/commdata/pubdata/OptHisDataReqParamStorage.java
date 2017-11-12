package com.commdata.pubdata;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javafx.util.Pair;

/**
 * 存储期权历史数据查询参数存储器
 */


public class OptHisDataReqParamStorage
{

    private ConcurrentLinkedQueue<Pair<Integer, OptionHistoricReqParams>> optHistReqParamQueue = new
            ConcurrentLinkedQueue();

    // 锁
    private final Lock lock = new ReentrantLock();
    // 仓库空的条件变量
    private final Condition empty = lock.newCondition();

    // 生产num个产品
    public void produce(Pair<Integer, OptionHistoricReqParams> reqidAndReqParam)
    {
        // 获得锁
        lock.lock();
        optHistReqParamQueue.offer(reqidAndReqParam);
        // 唤醒其他所有线程
        empty.signalAll();
        // 释放锁
        lock.unlock();
    }

    // 消费1个产品
    public void consume(List<Pair<Integer, OptionHistoricReqParams>> consumContLst)
    {
        // 获得锁
        lock.lock();
        // 如果仓库存储量不足
        while (optHistReqParamQueue.isEmpty())
        {
            try
            {
                // 由于条件不满足，消费阻塞
                empty.await();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        Pair<Integer, OptionHistoricReqParams> poll = optHistReqParamQueue.poll();
        if (consumContLst != null)
        {
            consumContLst.add(poll);
        }
        // 唤醒其他所有线程
        empty.signalAll();
        // 释放锁
        lock.unlock();
    }

}
