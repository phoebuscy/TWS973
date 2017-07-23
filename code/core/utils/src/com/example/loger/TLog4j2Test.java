package com.example.loger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by caiyong on 2017/3/29.
 */
public class TLog4j2Test
{
    private static Logger LogApp = LogManager.getLogger("applog");
    private static Logger LogMsg = LogManager.getLogger("datamsg");

    public static void main(String[] args)
    {
        int b = 1;
        for(int i = 0; i < 10000; i++)
        {
            LogApp.trace("trace level");
            LogApp.debug("debug level");
            LogApp.info("info level");
            LogApp.warn("warn level");
            LogApp.error("error level");
            LogApp.fatal("fatal level");


            LogMsg.trace("trace level2");
            LogMsg.debug("debug level2");
            LogMsg.info("info level2");
            LogMsg.warn("warn level2");
            LogMsg.error("error level2");
            LogMsg.fatal("fatal level2");
        }
        int a = 1;
    }
}
