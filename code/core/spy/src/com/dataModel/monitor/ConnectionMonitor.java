package com.dataModel.monitor;


import com.ib.client.EClientSocket;
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
    private static CheckConnection checkConnection = new CheckConnection();

    private static ConnectionMonitor instence = new ConnectionMonitor();

    public static ConnectionMonitor getInstence()
    {
        return instence;
    }

    public static void startMonitor(EClientSocket eClientSocket, String host, int port, int clientid)
    {
        checkConnection.setClient(eClientSocket, host, port, clientid);
        ConnectionMonitor.getInstence().monitor.scheduleAtFixedRate(checkConnection, 0, checkRate, TimeUnit.SECONDS);
    }

    static class CheckConnection implements Runnable
    {
        EClientSocket m_client;
        String m_host;
        int m_port;
        int m_clientid;

        public CheckConnection()
        {

        }

        public void setClient(EClientSocket client, String host, int port, int clientid)
        {
            m_client = client;
            m_host = host;
            m_port = port;
            m_clientid = clientid;
        }

        public EClientSocket getClient()
        {
            return m_client;
        }

        @Override
        public void run()
        {
            if(m_client != null)
            {
                if (m_client.isConnected() || m_client.isAsyncEConnect())
                {
                    TMbassadorSingleton.getInstance(DATAMAAGER_BUS).publish(makeAKmsg(AK_CONNECTED, "true"));
                    LogApp.info("ConnectionMonitor check result : connected");
                    // m_client.cancelOrder(-100);
                } else
                {
                    TMbassadorSingleton.getInstance(DATAMAAGER_BUS).publish(makeAKmsg(AK_CONNECTED, "false"));
                    LogApp.info("ConnectionMonitor check result : disconnected");

                    LogApp.info("ConnectionMonitor reconnect, host ");
                    m_client.eConnect(m_host, m_port, m_clientid);
                }
            }
            else
            {
                LogApp.error("ConnectionMonitor the m_client is null, can't check connection");
            }
        }
    }


}
