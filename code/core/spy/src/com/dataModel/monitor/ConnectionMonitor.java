package com.dataModel.monitor;


import com.dataModel.SDataManager;
import com.utils.TMbassadorSingleton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.utils.TConst.AK_CONNECTED;
import static com.utils.TConst.DATAMAAGER_BUS;
import static com.utils.TPubUtil.makeAKmsg;

/**
 * 检查联通性的监视器
 */

public class ConnectionMonitor
{
    private static Logger LogApp = LogManager.getLogger("applog");

    private static int checkRate = 10;  // 检查频率，单位 秒
    private static ScheduledExecutorService monitor = Executors.newSingleThreadScheduledExecutor();
    private static CheckConnection checkConnection;

    private static ConnectionMonitor instence = new ConnectionMonitor();

    public static ConnectionMonitor getInstence()
    {
        return instence;
    }

    public static void startMonitor(SDataManager dataManager)
    {
        if (dataManager != null)
        {
            checkConnection = new CheckConnection(dataManager);
            ConnectionMonitor.getInstence().monitor.scheduleAtFixedRate(checkConnection, 0, checkRate, TimeUnit.SECONDS);
        }
    }


    static class CheckConnection implements Runnable
    {
        SDataManager m_dataManager;

        public CheckConnection(SDataManager dataManager)
        {
            m_dataManager = dataManager;
        }


        @Override
        public void run()
        {
            if (m_dataManager != null)
            {
                if (m_dataManager.isConnected())
                {
                    TMbassadorSingleton.getInstance(DATAMAAGER_BUS).publish(makeAKmsg(AK_CONNECTED, "true"));
                    LogApp.info("ConnectionMonitor check result : connected");
                    m_dataManager.reqCurrentTime();
                } else
                {
                    TMbassadorSingleton.getInstance(DATAMAAGER_BUS).publish(makeAKmsg(AK_CONNECTED, "false"));
                    LogApp.info("ConnectionMonitor check result : disconnected");
                    LogApp.info("ConnectionMonitor reconnect, host ");

                    SDataManager.getInstance().conncet();
                    ProcessMsgMonitor.startProcessMsg(SDataManager.getInstance());
                }
            } else
            {
                LogApp.error("ConnectionMonitor the SDataManager is null, can't check connection");
            }
        }
    }


}
