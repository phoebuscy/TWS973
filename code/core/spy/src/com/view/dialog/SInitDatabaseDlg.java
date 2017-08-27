package com.view.dialog;

import com.utils.GBC;
import com.utils.SUtil;
import com.utils.TConst;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Dimension;
import java.awt.GridBagLayout;

import static com.utils.TFileUtil.getConfigValue;

public class SInitDatabaseDlg extends JFrame
{

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
        JLabel userLabel = new JLabel(getConfigValue("user.name",TConst.CONFIG_I18N_FILE));
        JTextField userTextField = new JTextField();
        JLabel pwLabel = new JLabel(getConfigValue("passworld", TConst.CONFIG_I18N_FILE));
        JTextField pwTextField = new JTextField();

        JButton confirmBtn = new JButton(getConfigValue("confirm",TConst.CONFIG_I18N_FILE));
        JButton cancelBtn = new JButton(getConfigValue("cancel", TConst.CONFIG_I18N_FILE));

        contentPnl.add(userLabel, new GBC(0,0));
        contentPnl.add(userTextField,new GBC(1,0));

        contentPnl.add(pwLabel,new GBC(0,1));
        contentPnl.add(pwTextField, new GBC(1,1));

        contentPnl.add(confirmBtn,new GBC(0,2));
        contentPnl.add(cancelBtn,new GBC(1,2));

        return contentPnl;
    }


}
