package com.view.dialog;

import com.utils.SUtil;
import com.utils.TPubUtil;
import com.view.panel.STopoFramContentPnl;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;

import javax.swing.JFrame;
import java.awt.Dimension;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import static com.utils.TFileUtil.getProjectFileByName;

/**
 * Created by 123 on 2016/12/18.
 */
public class SMainFram extends JFrame
{
    public static void main(String[] args)
    {
        SMainFram sMainFram = new SMainFram();
        sMainFram.setVisible(true);
    }

    public SMainFram()
    {
        init();
        buildTopoFrame();
    }


    private void init()
    {
        // log4j2默认在classpath下查找配置文件，可以修改配置文件的位置。
        String filename = "conf/log4j2.xml";
        List<String> fileLst = getProjectFileByName(filename);
        if (TPubUtil.notNullAndEmptyCollection(fileLst))
        {
            filename = fileLst.get(0);
        }
        File file = new File(filename);
        try
        {
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
            final ConfigurationSource source = new ConfigurationSource(in);
            Configurator.initialize(null, source);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void buildTopoFrame()
    {
        Dimension guiDim = SUtil.getGUIDimension((double) 4 / 5, (double) 4 / 5);
        setSize(guiDim);
        SUtil.showInScreenCenter(this, guiDim);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(new STopoFramContentPnl(this));
        SUtil.setWindosStyle(this, 1);

    }


}
