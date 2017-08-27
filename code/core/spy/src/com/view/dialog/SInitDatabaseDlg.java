package com.view.dialog;

import com.utils.GBC;
import com.utils.SUtil;
import com.utils.TConst;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    JTextField userTextField = new JTextField("root",20);
    JTextField pwTextField = new JTextField("try258TRY",20);

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
        //  JTextField userTextField = new JTextField(20);
        JLabel pwLabel = new JLabel(getConfigValue("passworld", TConst.CONFIG_I18N_FILE));
        // JTextField pwTextField = new JTextField(20);

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
            Connection connection = connectDB(userName, password);
            createDB(connection);
            int a = 1;
        }
    }

    /**
     * ���ӵ����ݿ�
     */
    private Connection connectDB(String userName, String password)
    {
        Connection conn = null;
        String url = "jdbc:mysql://localhost:3306/mysql?serverTimezone=UTC";
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
        }
        catch (ClassNotFoundException e)
        {
            // TODO Auto-generated catch block
            LogApp.error("add mysql jdbc driver failed");  // �Ҳ���������
            e.printStackTrace();
        }
        try
        {
            conn = DriverManager.getConnection(url, userName, password);
            if (conn != null)
            {
                LogApp.info("connection successful");
            }
        }
        catch (SQLException e)
        {
            // TODO Auto-generated catch block
            LogApp.error("connection fail");
            e.printStackTrace();
        }
        return conn;
    }

    private void createDB(Connection conn)
    {
        if(conn != null)
        {
            try
            {
                java.sql.Statement statement = conn.createStatement();
                String dropDb = "";
                String hrappSQL = "CREATE DATABASE  IF NOT EXISTS twsdb";  // ����IF NOT EXISTS�������ݿ��Ѿ����ڣ���ԭ���ĸ��ǵ���
                int ret = statement.executeUpdate(hrappSQL);
                int a = 1;
            }
            catch (SQLException ex)
            {
                ex.printStackTrace();
            }

        }
    }


}
