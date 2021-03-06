package com.view.dialog;

import com.utils.SUtil;
import com.utils.TConst;
import com.utils.TPubUtil;
import com.view.panel.STopoFramContentPnl;
import java.awt.Dimension;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import static com.utils.TFileUtil.getConfigValue;
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


        setMenuBar();
    }

    private void buildTopoFrame()
    {
        Dimension guiDim = SUtil.getGUIDimension(1.0, 1.0);
        setSize(guiDim);
        SUtil.showInScreenCenter(this, guiDim);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(new STopoFramContentPnl(this));
        SUtil.setWindosStyle(this, 1);
    }


    private void setMenuBar()
    {
        JMenuBar bar = new JMenuBar();
        JMenu menuFile = new JMenu(getConfigValue("file", TConst.CONFIG_I18N_FILE));
        JMenuItem itemConfig = new JMenuItem(getConfigValue("config", TConst.CONFIG_I18N_FILE));
        JMenuItem itemAuto = new JMenuItem(getConfigValue("auto.buy.sale", TConst.CONFIG_I18N_FILE));
        JMenuItem itemExit = new JMenuItem(getConfigValue("exit", TConst.CONFIG_I18N_FILE));
        menuFile.add(itemConfig);
        menuFile.add(itemAuto);
        menuFile.add(itemExit);
        itemConfig.addActionListener(e -> {
            SSettingDlg settingDlg = new SSettingDlg();
            settingDlg.setVisible(true);
        });

        // 自动化操作
        itemAuto.addActionListener(e ->
        {
            SAutoBuyOrSaleDlg autoBuyOrSale = new SAutoBuyOrSaleDlg();
            autoBuyOrSale.setVisible(true);

        });
        bar.add(menuFile);

        JMenu menuDatabase = new JMenu(getConfigValue("database", TConst.CONFIG_I18N_FILE));
        JMenuItem itemInitDatabase = new JMenuItem(getConfigValue("init.database", TConst.CONFIG_I18N_FILE));
        itemInitDatabase.addActionListener(e -> {
            SInitDatabaseDlg initDatabaseDlg = new SInitDatabaseDlg();
            initDatabaseDlg.setVisible(true);
        });
        menuDatabase.add(itemInitDatabase);
        JMenuItem itemInitHistoricData = new JMenuItem(getConfigValue("init.historic.data", TConst.CONFIG_I18N_FILE));
        menuDatabase.add(itemInitHistoricData);
        itemInitHistoricData.addActionListener(e -> {

            SInitHistoricDataDlg initHistoricDataDlg = new SInitHistoricDataDlg();
            initHistoricDataDlg.setVisible(true);

        });

        bar.add(menuDatabase);

        setJMenuBar(bar);

    }


}
