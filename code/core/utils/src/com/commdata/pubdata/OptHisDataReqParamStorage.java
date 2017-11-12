package com.commdata.pubdata;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javafx.util.Pair;

/**
 * �洢��Ȩ��ʷ���ݲ�ѯ�����洢��
 */


public class OptHisDataReqParamStorage
{

    private ConcurrentLinkedQueue<Pair<Integer, OptionHistoricReqParams>> optHistReqParamQueue = new
            ConcurrentLinkedQueue();

    // ��
    private final Lock lock = new ReentrantLock();
    // �ֿ�յ���������
    private final Condition empty = lock.newCondition();

    // ����num����Ʒ
    public void produce(Pair<Integer, OptionHistoricReqParams> reqidAndReqParam)
    {
        // �����
        lock.lock();
        optHistReqParamQueue.offer(reqidAndReqParam);
        // �������������߳�
        empty.signalAll();
        // �ͷ���
        lock.unlock();
    }

    // ����1����Ʒ
    public void consume(List<Pair<Integer, OptionHistoricReqParams>> consumContLst)
    {
        // �����
        lock.lock();
        // ����ֿ�洢������
        while (optHistReqParamQueue.isEmpty())
        {
            try
            {
                // �������������㣬��������
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
        // �������������߳�
        empty.signalAll();
        // �ͷ���
        lock.unlock();
    }

}
