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
本dialog的作用是创建数据库及创建表
1： 创建数据库  ：
    twsdb

2： 创建表：
    thirtysec      // 30秒记录数据表
    oneminute      // 1分表
    twominute      // 2 分表
    fiveminute     // 5分表
    fifteenminute  // 15分表
    thirtyminute   // 30分表
    onehour        // 1小时表
    oneday         //

    ///////
    reqid          // 每次操作id表
    operrecord     // 操作记录表

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
        String ifcontinue = getConfigValue("if.continue", TConst.CONFIG_I18N_FILE); // "是否继续"
        int ret = JOptionPane.showConfirmDialog(null, delDatabaseInfo, ifcontinue, JOptionPane.YES_NO_OPTION);
        if (ret == 0) // 0为YES
        {
            String userName = userTextField.getText().trim();
            String password = pwTextField.getText().trim();
            dbManager.initDb(userName,password);

            /*
            Connection connection = connectDB(userName, password);
            //  createDB(connection);  // 创建数据库
            // createTable(userName, password); // 创建数据库表
            // inserData(userName, password);
            createReqIDTable(userName, password);  // 创建reqid表
            inserDataByProc(userName, password);  // 调用存储过程插入数据
            */
            int a = 1;
        }
    }


    /**
     * 连接到数据库
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
            LogApp.error("add mysql jdbc driver failed");  // 找不到驱动！
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
     * 创建数据库
     *
     * @param conn 数据库链接
     */
    private void createDB(Connection conn)
    {
        if (conn != null)
        {
            try
            {
                Statement statement = conn.createStatement();
                String hrappSQL = "CREATE DATABASE  IF NOT EXISTS twsdb";  // 加上IF NOT EXISTS就算数据库已经存在，把原来的覆盖掉了
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
     * 创建数据库表
     * @param
     */
    private void createTable(String username, String password)
    {
        try
        {
            //加载驱动
            Class.forName("com.mysql.jdbc.Driver");
            //链接到数据库
            String url = "jdbc:mysql://localhost:3306/" + dbname + "?serverTimezone=UTC";
            Connection ss = DriverManager.getConnection(url, username, password);
            //获取对象
            Statement stmt = ss.createStatement();
            //插入记录到数据库中
            String sqlstr = "create table operrecord(id int(1),name varchar(20),opertime DATETIME);";
            int ret = stmt.executeUpdate(sqlstr);

            System.out.println("创建成功！");
            stmt.close();
            ss.close();
        } catch (Exception e)
        {
            System.out.println("创建失败！或已经存在该表格" + e);
            e.printStackTrace();
        }
    }

    /**
     * 创建查询id表
     * @param username
     * @param password
     */
   private void createReqIDTable(String username, String password)
   {
       try
       {
           //加载驱动
           Class.forName("com.mysql.jdbc.Driver");
           //链接到数据库
           String url = "jdbc:mysql://localhost:3306/" + dbname + "?serverTimezone=UTC";
           Connection ss = DriverManager.getConnection(url, username, password);
           //获取对象
           Statement stmt = ss.createStatement();
           //插入记录到数据库中
           String sqlstr = "create table reqid(id int(1));";
           int ret = stmt.executeUpdate(sqlstr);

           System.out.println("创建成功！");
           stmt.close();
           ss.close();
       } catch (Exception e)
       {
           System.out.println("创建失败！或已经存在该表格" + e);
           e.printStackTrace();
       }

   }

    private void inserData(String username, String password)
    {
        try
        {
            //加载驱动
            Class.forName("com.mysql.jdbc.Driver");
            //链接到数据库
            String url = "jdbc:mysql://localhost:3306/" + dbname + "?serverTimezone=UTC";
            Connection ss = DriverManager.getConnection(url, username, password);
            //获取对象
            Statement stmt = ss.createStatement();
            //插入记录到数据库中 , 5000条
            for (int i = 0; i < 5000; i++)
            {
                String sqlstr = "insert into operrecord values(" + String.valueOf(i) + ", 'abc','2018-08-01 22:16:27')";
                int ret = stmt.executeUpdate(sqlstr);
            }

            stmt.close();
            ss.close();
        } catch (Exception e)
        {
            System.out.println("创建失败！或已经存在该表格" + e);
            e.printStackTrace();
        }

    }

    private void inserDataByProc(String username, String password)
    {
        try
        {
            //加载驱动
            Class.forName("com.mysql.jdbc.Driver");
            //链接到数据库
            String url = "jdbc:mysql://localhost:3306/" + dbname + "?serverTimezone=UTC";
            Connection connection = DriverManager.getConnection(url, username, password);

        //    String sql = "{CALL proc_insertdata(?,?)}"; //调用存储过程
          //  CallableStatement cstm = connection.prepareCall(sql); //实例化对象cstm
            //插入记录到数据库中 , 5000条
            for (int i = 0; i < 5000; i++)
            {
                //  String sqlstr = "insert into operrecord values(" + String.valueOf(i) + ", 'abc','2018-08-01 22:16:27')";
                // int ret = stmt.executeUpdate(sqlstr);

                String sql = "{CALL proc_insertdata(?,?)}"; //调用存储过程
                CallableStatement cstm = connection.prepareCall(sql); //实例化对象cstm
                cstm.setInt(1, i); //存储过程输入参数
                cstm.setString(2, String.valueOf(i)); // 存储过程输入参数
               // cstm.setObject(3, "2018-08-01 22:16:27");
                cstm.execute(); // 执行存储过程
            }

           // cstm.close();
            connection.close();
        } catch (Exception e)
        {
            System.out.println("创建失败！或已经存在该表格" + e);
            e.printStackTrace();
        }

    }
}
