package com.view.dialog;

import com.database.DbManager;
import com.utils.GBC;
import com.utils.SUtil;
import com.utils.TConst;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.time.LocalDate;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import static com.utils.TFileUtil.getConfigValue;

public class SInitHistoricDataDlg extends JFrame
{
    private static Logger LogApp = LogManager.getLogger("applog");
    private DbManager dbManager = DbManager.getInstance();


    JTextField dateFromField = new JTextField(LocalDate.now().toString(), 10);
    JTextField dateToField = new JTextField(LocalDate.now().toString(), 10);
    JTextArea infoTextArea = new JTextArea();
    JComboBox barSizeCombox = new JComboBox();


    public SInitHistoricDataDlg()
    {
        initFrame();
    }


    private void initFrame()
    {
        Dimension guiDim = SUtil.getGUIDimension((double) 2 / 5, (double) 1 / 3);
        setTitle(getConfigValue("init.historic.data", TConst.CONFIG_I18N_FILE));
        setSize(guiDim);
        SUtil.showInScreenCenter(this, guiDim);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setContentPane(crtContentPnl());
        SUtil.setWindosStyle(this, 1);
    }

    private Container crtContentPnl()
    {
        JPanel contentPnl = new JPanel(new GridBagLayout());
        JPanel paramPanel = getQuryParamPanel();
        JPanel showInfoPanel = getShowInfoPanel();
        contentPnl.add(paramPanel, new GBC(0, 0).setWeight(1, 1).setFill(GBC.BOTH).setInsets(6));
        contentPnl.add(showInfoPanel, new GBC(0, 1).setWeight(1, 10).setFill(GBC.BOTH).setInsets(6));
        return contentPnl;
    }

    private JPanel getShowInfoPanel()
    {
        JPanel showInfoPnl = new JPanel(new BorderLayout());
        JScrollPane js = new JScrollPane(infoTextArea);
        js.setBounds(13, 10, 350, 340);
        //默认的设置是超过文本框才会显示滚动条，以下设置让滚动条一直显示
        js.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        showInfoPnl.add(js, BorderLayout.CENTER);
        return showInfoPnl;
    }

    private JPanel getQuryParamPanel()
    {
        JPanel paramPnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        JLabel date = new JLabel(getConfigValue("date", TConst.CONFIG_I18N_FILE), 10);
        JLabel dateFrom = new JLabel(getConfigValue("date.from", TConst.CONFIG_I18N_FILE), 10);
        JLabel dateTo = new JLabel(getConfigValue("date.to", TConst.CONFIG_I18N_FILE), 10);
        JLabel barSizeLabel = new JLabel("BarSize", 10);
        barSizeCombox.setPreferredSize(new Dimension(150, 30));
        setBarSizeComBoxItem(barSizeCombox);
        JButton queryHisDataBtn = new JButton(getConfigValue("get.historic.data", TConst.CONFIG_I18N_FILE));
        queryHisDataBtn.addActionListener(e -> {
            queryHisData();
        });

        paramPnl.add(date);
        paramPnl.add(dateFrom);
        paramPnl.add(dateFromField);
        paramPnl.add(dateTo);
        paramPnl.add(dateToField);
        paramPnl.add(barSizeLabel);
        paramPnl.add(barSizeCombox);
        paramPnl.add(queryHisDataBtn);
        return paramPnl;
    }

    private void setBarSizeComBoxItem(JComboBox barSizeCombox)
    {
        if(barSizeCombox != null)
        {
            barSizeCombox.removeAllItems();
            barSizeCombox.addItem(com.ib.client.Types.BarSize._5_secs);
            barSizeCombox.addItem(com.ib.client.Types.BarSize._15_secs);
            barSizeCombox.addItem(com.ib.client.Types.BarSize._30_secs);
            barSizeCombox.addItem(com.ib.client.Types.BarSize._1_min);
            barSizeCombox.addItem(com.ib.client.Types.BarSize._2_mins);
            barSizeCombox.addItem(com.ib.client.Types.BarSize._3_mins);
            barSizeCombox.addItem(com.ib.client.Types.BarSize._5_mins);
            barSizeCombox.addItem(com.ib.client.Types.BarSize._15_mins);
            barSizeCombox.addItem(com.ib.client.Types.BarSize._30_mins);
            barSizeCombox.addItem(com.ib.client.Types.BarSize._1_hour);
            barSizeCombox.addItem(com.ib.client.Types.BarSize._1_day);
        }
    }


    private void queryHisData()
    {
        infoTextArea.append("asdfasdA\n");

    }


}
