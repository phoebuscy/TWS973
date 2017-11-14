package com.view.dialog;

import com.database.DbManager;
import com.utils.GBC;
import com.utils.SUtil;
import com.utils.TConst;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
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
    private DbManager dbManager = DbManager.getInstance();

    private final String dbname = "twsdb";

    JTextField userTextField = new JTextField("root", 20);
    JTextField pwTextField = new JTextField("try258TRY", 20);

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
            dbManager.initDb(userName,password);

            /*
            Connection connection = connectDB(userName, password);
            //  createDB(connection);  // �������ݿ�
            // createTable(userName, password); // �������ݿ��
            // inserData(userName, password);
            createReqIDTable(userName, password);  // ����reqid��
            inserDataByProc(userName, password);  // ���ô洢���̲�������
            */
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
        } catch (ClassNotFoundException e)
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
        } catch (SQLException e)
        {
            // TODO Auto-generated catch block
            LogApp.error("connection fail");
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * �������ݿ�
     *
     * @param conn ���ݿ�����
     */
    private void createDB(Connection conn)
    {
        if (conn != null)
        {
            try
            {
                Statement statement = conn.createStatement();
                String hrappSQL = "CREATE DATABASE  IF NOT EXISTS twsdb";  // ����IF NOT EXISTS�������ݿ��Ѿ����ڣ���ԭ���ĸ��ǵ���
                int ret = statement.executeUpdate(hrappSQL);
                statement.close();
                conn.close();
            } catch (SQLException ex)
            {
                ex.printStackTrace();
            }

        }
    }

    /**
     * �������ݿ��
     * @param
     */
    private void createTable(String username, String password)
    {
        try
        {
            //��������
            Class.forName("com.mysql.jdbc.Driver");
            //���ӵ����ݿ�
            String url = "jdbc:mysql://localhost:3306/" + dbname + "?serverTimezone=UTC";
            Connection ss = DriverManager.getConnection(url, username, password);
            //��ȡ����
            Statement stmt = ss.createStatement();
            //�����¼�����ݿ���
            String sqlstr = "create table operrecord(id int(1),name varchar(20),opertime DATETIME);";
            int ret = stmt.executeUpdate(sqlstr);

            System.out.println("�����ɹ���");
            stmt.close();
            ss.close();
        } catch (Exception e)
        {
            System.out.println("����ʧ�ܣ����Ѿ����ڸñ��" + e);
            e.printStackTrace();
        }
    }

    /**
     * ������ѯid��
     * @param username
     * @param password
     */
   private void createReqIDTable(String username, String password)
   {
       try
       {
           //��������
           Class.forName("com.mysql.jdbc.Driver");
           //���ӵ����ݿ�
           String url = "jdbc:mysql://localhost:3306/" + dbname + "?serverTimezone=UTC";
           Connection ss = DriverManager.getConnection(url, username, password);
           //��ȡ����
           Statement stmt = ss.createStatement();
           //�����¼�����ݿ���
           String sqlstr = "create table reqid(id int(1));";
           int ret = stmt.executeUpdate(sqlstr);

           System.out.println("�����ɹ���");
           stmt.close();
           ss.close();
       } catch (Exception e)
       {
           System.out.println("����ʧ�ܣ����Ѿ����ڸñ��" + e);
           e.printStackTrace();
       }

   }

    private void inserData(String username, String password)
    {
        try
        {
            //��������
            Class.forName("com.mysql.jdbc.Driver");
            //���ӵ����ݿ�
            String url = "jdbc:mysql://localhost:3306/" + dbname + "?serverTimezone=UTC";
            Connection ss = DriverManager.getConnection(url, username, password);
            //��ȡ����
            Statement stmt = ss.createStatement();
            //�����¼�����ݿ��� , 5000��
            for (int i = 0; i < 5000; i++)
            {
                String sqlstr = "insert into operrecord values(" + String.valueOf(i) + ", 'abc','2018-08-01 22:16:27')";
                int ret = stmt.executeUpdate(sqlstr);
            }

            stmt.close();
            ss.close();
        } catch (Exception e)
        {
            System.out.println("����ʧ�ܣ����Ѿ����ڸñ��" + e);
            e.printStackTrace();
        }

    }

    private void inserDataByProc(String username, String password)
    {
        try
        {
            //��������
            Class.forName("com.mysql.jdbc.Driver");
            //���ӵ����ݿ�
            String url = "jdbc:mysql://localhost:3306/" + dbname + "?serverTimezone=UTC";
            Connection connection = DriverManager.getConnection(url, username, password);

        //    String sql = "{CALL proc_insertdata(?,?)}"; //���ô洢����
          //  CallableStatement cstm = connection.prepareCall(sql); //ʵ��������cstm
            //�����¼�����ݿ��� , 5000��
            for (int i = 0; i < 5000; i++)
            {
                //  String sqlstr = "insert into operrecord values(" + String.valueOf(i) + ", 'abc','2018-08-01 22:16:27')";
                // int ret = stmt.executeUpdate(sqlstr);

                String sql = "{CALL proc_insertdata(?,?)}"; //���ô洢����
                CallableStatement cstm = connection.prepareCall(sql); //ʵ��������cstm
                cstm.setInt(1, i); //�洢�����������
                cstm.setString(2, String.valueOf(i)); // �洢�����������
               // cstm.setObject(3, "2018-08-01 22:16:27");
                cstm.execute(); // ִ�д洢����
            }

           // cstm.close();
            connection.close();
        } catch (Exception e)
        {
            System.out.println("����ʧ�ܣ����Ѿ����ڸñ��" + e);
            e.printStackTrace();
        }

    }
}
