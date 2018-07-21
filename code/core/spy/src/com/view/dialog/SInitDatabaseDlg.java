package com.view.dialog;

import com.database.DbManager;
import com.utils.GBC;
import com.utils.SUtil;
import com.utils.TConst;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import static com.utils.TFileUtil.getConfigValue;

/*
��dialog�������Ǵ������ݿ⼰������
1�� �������ݿ�  ��
    twsdb

2�� ������
    thirtysec      // 30���¼���ݱ�
    oneminute      // 1�ֱ�
    twominute      // 2 �ֱ�
    fiveminute     // 5�ֱ�
    fifteenminute  // 15�ֱ�
    thirtyminute   // 30�ֱ�
    onehour        // 1Сʱ��
    oneday         //

    ///////
    reqid          // ÿ�β���id��
    operrecord     // ������¼��

 */

public class SInitDatabaseDlg extends JFrame
{
    private static Logger LogApp = LogManager.getLogger("applog");

    JTextField userTextField = new JTextField("root", 20);
    JTextField pwTextField = new JTextField("@try258TRY", 20);

    public SInitDatabaseDlg()
    {
        initFrame();
    }


    private void initFrame()
    {
        Dimension guiDim = SUtil.getGUIDimension((double) 1 / 3, (double) 1 / 3);
        setTitle(getConfigValue("init.database", TConst.CONFIG_I18N_FILE));
        setSize(guiDim);
        SUtil.showInScreenCenter(this, guiDim);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setContentPane(crtContentPnl());
        SUtil.setWindosStyle(this, 1);
    }


    private JPanel crtContentPnl()
    {
        JPanel contentPnl = new JPanel();
        contentPnl.setLayout(new GridBagLayout());
        JLabel userLabel = new JLabel(getConfigValue("user.name", TConst.CONFIG_I18N_FILE));
        JLabel pwLabel = new JLabel(getConfigValue("passworld", TConst.CONFIG_I18N_FILE));

        JButton confirmBtn = new JButton(getConfigValue("confirm", TConst.CONFIG_I18N_FILE));
        confirmBtn.addActionListener(new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                initdatabase();
            }
        });
        JButton cancelBtn = new JButton(getConfigValue("cancel", TConst.CONFIG_I18N_FILE));
        contentPnl.add(userLabel, new GBC(0, 0));
        contentPnl.add(userTextField, new GBC(1, 0));
        contentPnl.add(pwLabel, new GBC(0, 1));
        contentPnl.add(pwTextField, new GBC(1, 1));
        contentPnl.add(confirmBtn, new GBC(0, 2));
        contentPnl.add(cancelBtn, new GBC(1, 2));
        return contentPnl;
    }

    private void initdatabase()
    {
        String delDatabaseInfo = getConfigValue("del.database.if.confirm", TConst.CONFIG_I18N_FILE);
        String ifcontinue = getConfigValue("if.continue", TConst.CONFIG_I18N_FILE); // "�Ƿ����"
        int ret = JOptionPane.showConfirmDialog(null, delDatabaseInfo, ifcontinue, JOptionPane.YES_NO_OPTION);
        if (ret == 0) // 0ΪYES
        {
            String userName = userTextField.getText().trim();
            String password = pwTextField.getText().trim();

            DbManager dbManager = DbManager.getInstance();
            dbManager.initDb(userName, password);

            int a = 1;
        }
    }





}
