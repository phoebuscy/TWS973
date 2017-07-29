package com.dataModel.monitor;

import com.dataModel.SDataManager;
import com.ib.client.EClientSocket;
import com.ib.client.EJavaSignal;
import com.ib.client.EReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProcessMsgMonitor
{
    private static Logger logApp = LogManager.getLogger("applog");

    private static ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();

    private static ProcessMsgMonitor instence = new ProcessMsgMonitor();

    public static ProcessMsgMonitor getInstence()
    {
        return instence;
    }

    public static void startProcessMsg(SDataManager dataManager)
    {
        if (dataManager != null)
        {
            ProcessMsgRunnable processMsgRunnable = new ProcessMsgRunnable(dataManager, logApp);
            singleThreadExecutor.execute(processMsgRunnable);
        }
    }

    static class ProcessMsgRunnable implements Runnable
    {
        SDataManager dataManager;
        Logger logApp;

        public ProcessMsgRunnable(SDataManager dataManager, Logger logApp)
        {
            this.dataManager = dataManager;
            this.logApp = logApp;
        }

        @Override
        public void run()
        {
            while (dataManager != null && dataManager.isConnected())
            {
                EJavaSignal signal = dataManager.getM_signal();
                EReader reader = dataManager.getM_reader();
                if (signal != null && reader != null)
                {
                    signal.waitForSignal();
                    try
                    {
                        reader.processMsgs();
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                        logApp.error(e.getMessage());
                    }
                } else
                {
                    logApp.error("ProcessMsgMonitor -> signal or reader is null");
                }
            }
        }
    }


}
