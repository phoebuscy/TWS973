package example.loger;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.util.List;

import static com.TFileUtil.getProjectFileByName;
import static com.TPubUtil.notNullAndEmptyCollection;

/**
 * Created by caiyong on 2017/3/27.
 */
public class Log4jTest
{
    private static Logger logger = Logger.getLogger(Log4jTest.class);

    public static void main(String[] args)
    {


        List<String> logprop = getProjectFileByName("log4j.properties");
        if (notNullAndEmptyCollection(logprop))
        {
            PropertyConfigurator.configure(logprop.get(0));
        }
        for(int i = 0; i < 10000; i++)
        {
            // 记录debug级别的信息
            logger.debug("This is debug message.");
            // 记录info级别的信息
            logger.info("This is info message.");
            // 记录error级别的信息
            logger.error("This is error message.");
        }

        int a = 1;



    }
}
