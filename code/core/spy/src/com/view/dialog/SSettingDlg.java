package com.view.dialog;

import com.dataModel.SDataManager;
import com.dataModel.Symbol;
import com.utils.GBC;
import com.utils.SUtil;
import com.utils.TConst;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import static com.utils.SUtil.isIntOrDoubleNumber;
import static com.utils.TFileUtil.getSPYCONFIGContents;
import static com.utils.TFileUtil.getConfigValue;
import static com.utils.TFileUtil.setSpyConfigFile;
import static com.utils.TPubUtil.notNullAndEmptyMap;
import static com.utils.TStringUtil.notNullAndEmptyStr;

public class SSettingDlg extends JFrame
{

    private JTextField onceMoneyTextField = new JTextField("1000", 20);

    private Symbol symbol = SDataManager.getInstance().getSymbol();

    public SSettingDlg()
    {
        initFrame();
    }


    private void initFrame()
    {
        Dimension guiDim = SUtil.getGUIDimension((double) 1 / 3, (double) 1 / 3);
        setTitle(getConfigValue("config", TConst.CONFIG_I18N_FILE));
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
        JLabel onceMoneyLabel = new JLabel(getConfigValue("onceMoney", TConst.CONFIG_I18N_FILE));
        JButton confirmBtn = new JButton(getConfigValue("confirm", TConst.CONFIG_I18N_FILE));
        onceMoneyTextField.setText(String.format("%.0f", getOnceMoneyConfigValue()));
        confirmBtn.addActionListener(new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String onceMoneyStr = onceMoneyTextField.getText().trim();
                if (isIntOrDoubleNumber(onceMoneyStr))
                {
                    setSpyConfigFile("symbol", "oncemoney", onceMoneyStr);
                    symbol.setOnceOperateMoney(Double.valueOf(onceMoneyStr));
                    JOptionPane.showMessageDialog(onceMoneyTextField, getConfigValue("saved", TConst.CONFIG_I18N_FILE));
                    setVisible(false);
                }
                else
                {
                    JOptionPane.showMessageDialog(onceMoneyTextField,
                                                  getConfigValue("input.has.error", TConst.CONFIG_I18N_FILE));
                }
            }
        });
        JButton cancelBtn = new JButton(getConfigValue("cancel", TConst.CONFIG_I18N_FILE));
        cancelBtn.addActionListener(new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                setVisible(false);
            }
        });
        contentPnl.add(onceMoneyLabel, new GBC(0, 0));
        contentPnl.add(onceMoneyTextField, new GBC(1, 0));
        contentPnl.add(confirmBtn, new GBC(0, 2));
        contentPnl.add(cancelBtn, new GBC(1, 2));
        return contentPnl;
    }

    private double getOnceMoneyConfigValue()
    {
        Map<String, String> cfgmap = getSPYCONFIGContents();
        if (notNullAndEmptyMap(cfgmap))
        {
            String oncemoney = cfgmap.get("oncemoney");
            double dbMoney = notNullAndEmptyStr(oncemoney) ? Double.valueOf(oncemoney) : 1000d;
            return dbMoney;
        }
        return 1000d;
    }

}
