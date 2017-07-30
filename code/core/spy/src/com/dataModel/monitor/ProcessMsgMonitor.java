package com.dataModel.monitor;

import com.dataModel.SDataManager;
import com.ib.client.EJavaSignal;
import com.ib.client.EReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProcessMsgMonitor
{
    private static Logger logApp = LogManager.getLogger("applog");
    private static boolean started = false;
    private static ExecutorService msgProcessThreadPool;

    public static void setStartedFlag(boolean isstarted)
    {
        started = isstarted;
    }


    public static void startMonitor(SDataManager dataManager)
    {
        if (dataManager.isConnected() && !started)
        {
            ProcessMsgRunnable processMsgRunnable = new ProcessMsgRunnable(dataManager, logApp);
            msgProcessThreadPool = Executors.newSingleThreadExecutor();
            msgProcessThreadPool.submit(processMsgRunnable);
            started = true;
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
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        logApp.error(e.getMessage());
                    }
                }
                else
                {
                    logApp.error("ProcessMsgMonitor -> signal or reader is null");
                }
            }
        }
    }


}
