package com.database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import static com.utils.TStringUtil.trimStr;

public class DbManager
{
    private static Logger LogApp = LogManager.getLogger("applog");
    private final String dbname = "twsdb";
    private String userName = "root";
    private String password = "try258TRY";
    private Connection connection;

    private static DbManager instance = new DbManager();


    public static void main(String[] args)
    {
        com.ib.client.Types.BarSize barSize = com.ib.client.Types.BarSize._2_mins;
        String str = barSize.toString().trim();

        int a = 1;
    }


    private DbManager()
    {
        connection = connectDB(dbname, userName, password);
    }

    public static DbManager getInstance()
    {
        return instance;
    }

    public void initDb(String userName, String password)
    {
        this.userName = userName;
        this.password = password;
        Connection mysqlConn = connectMySql(userName, password);
        createTwsDb(mysqlConn, dbname);  // ����������򴴽�TwsDb���ݿ�
        connection = connectDB(dbname, userName, password);
        createTables(connection);  // �������ֱ�
        createProcedure(connection);
    }

    // �������ݿ�ĸ��ֱ�
    private void createTables(Connection connection)
    {
        // ��ѯid��
        createQueryIDTable(connection);
        // ��ʷ���ݱ�
        createHistoricDataIDTable(connection);
    }

    // �������ݿ�Ĵ洢����
    private void createProcedure(Connection connection)
    {
        createQueryReqIdProc(connection);
    }

    private void createTwsDb(Connection mysqlConn, String dbname)
    {
        if (mysqlConn != null)
        {
            try
            {
                Statement statement = mysqlConn.createStatement();
                String hrappSQL = "CREATE DATABASE  IF NOT EXISTS " + dbname;  // ����IF NOT EXISTS�������ݿ��Ѿ����ڣ���ԭ���ĸ��ǵ���
                int ret = statement.executeUpdate(hrappSQL);
                statement.close();
                mysqlConn.close();
            }
            catch (SQLException ex)
            {
                LogApp.error("DbManager createTwsDb failed");
                ex.printStackTrace();
            }
        }
    }

    /**
     * ���ӵ����ݿ�
     */
    private Connection connectDB(String dbname, String userName, String password)
    {
        Connection connection = null;
        String url = "jdbc:mysql://localhost:3306/" + dbname + "?serverTimezone=UTC";
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
            connection = DriverManager.getConnection(url, userName, password);
            if (connection != null)
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
        return connection;
    }

    /**
     * ���ӵ����ݿ�
     */
    private Connection connectMySql(String userName, String password)
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

    /**
     * ������ѯid��
     *
     * @param connection
     */
    private void createQueryIDTable(Connection connection)
    {
        if (connection != null)
        {
            try
            {
                //��������
                Class.forName("com.mysql.jdbc.Driver");
                //���ӵ����ݿ�
                String url = "jdbc:mysql://localhost:3306/" + dbname + "?serverTimezone=UTC";
                //��ȡ����
                Statement stmt = connection.createStatement();
                String delTableSqlStr = "drop table if exists queryidtable;";
                stmt.execute(delTableSqlStr);

                //�����¼�����ݿ���
                String sqlstr = "create table queryidtable(reqid int(1));";
                int ret = stmt.executeUpdate(sqlstr);

                String sqlInsertData = "insert into queryidtable values(100000000)";
                stmt.executeUpdate(sqlInsertData);

                System.out.println("�����ɹ���");
                stmt.close();
            }
            catch (Exception e)
            {
                System.out.println("����ʧ�ܣ����Ѿ����ڸñ��" + e);
                e.printStackTrace();
            }
        }
    }

    /**
     * ������ѯreqid�Ĵ洢����
     *
     * @param connection
     */
    private void createQueryReqIdProc(Connection connection)
    {
        if (connection != null)
        {
            try
            {
                Statement statement = connection.createStatement();
                String sqlStr = "create procedure queryReqIDProc(out id int)" + "BEGIN " +
                                "update queryidtable set reqid=reqid+1;" + "select reqid into id from queryidtable;" +
                                "END ";
                statement.executeUpdate(sqlStr);
            }
            catch (SQLException e)
            {
                LogApp.error("DbManager createQueryReqIdProc failed");
                e.printStackTrace();
            }
        }
    }

    public int queryReqID()
    {
        int retid = 0;
        if (connection != null)
        {
            try
            {
                CallableStatement cs = connection.prepareCall("{call queryReqIDProc(?)}");
                cs.registerOutParameter(1, Types.INTEGER);
                cs.execute();
                retid = cs.getInt(1);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }

        }
        return retid;
    }

    /**
     *   ������ʷ���ݱ� ,��Ĺ������
     * 5 ��
     15 ��
     30 ��
     1 ����
     2 ����
     3 ����
     5 ����
     15 ����
     30 ����
     1 Сʱ
     1 ��
     * @param connection ���ݿ�����
     */
    private void createHistoricDataIDTable(Connection connection)
    {
        createHistoricDataIDTable(connection,com.ib.client.Types.BarSize._5_secs);
        createHistoricDataIDTable(connection,com.ib.client.Types.BarSize._15_secs);
        createHistoricDataIDTable(connection,com.ib.client.Types.BarSize._30_secs);
        createHistoricDataIDTable(connection,com.ib.client.Types.BarSize._1_min);
        createHistoricDataIDTable(connection,com.ib.client.Types.BarSize._2_mins);
        createHistoricDataIDTable(connection,com.ib.client.Types.BarSize._3_mins);
        createHistoricDataIDTable(connection,com.ib.client.Types.BarSize._5_mins);
        createHistoricDataIDTable(connection,com.ib.client.Types.BarSize._15_mins);
        createHistoricDataIDTable(connection,com.ib.client.Types.BarSize._30_mins);
        createHistoricDataIDTable(connection,com.ib.client.Types.BarSize._1_hour);
        createHistoricDataIDTable(connection,com.ib.client.Types.BarSize._1_day);
    }

    /**
     * ������ʷ���ݱ� ,��Ĺ������
     * @param connection ���ݿ�����
     * @param barSize ��
     */
    private void createHistoricDataIDTable(Connection connection, com.ib.client.Types.BarSize barSize)
    {
        if (connection != null)
        {
            try
            {
                //��������
                Class.forName("com.mysql.jdbc.Driver");
                //���ӵ����ݿ�
                String url = "jdbc:mysql://localhost:3306/" + dbname + "?serverTimezone=UTC";
                //��ȡ����
                Statement stmt = connection.createStatement();
                String tableName = "hisdata" + trimStr(barSize.toString());
                //  String delTableSqlStr = "drop table if exists" + tableName + ";";
                //  stmt.execute(delTableSqlStr);

                String sqlstr2 = "create table queryidtable(reqid int(1));";
                String sqlstr = "create table" + " " +  tableName + " " + crtHistoricDataFileStatement();
                int ret = stmt.executeUpdate(sqlstr);

                System.out.println("�����ɹ���");
                stmt.close();
            }
            catch (Exception e)
            {
                System.out.println("����ʧ�ܣ����Ѿ����ڸñ��" + e);
                e.printStackTrace();
            }
        }
    }

    /**
     * ������ʷ���ݵ��ֶ�
     * public String date;
     public double open;
     public double high;
     public double low;
     public double close;
     public int volume;
     public int count;
     public double WAP;
     public boolean hasGaps;
     * @return
     */
    private String crtHistoricDataFileStatement()
    {
        StringBuilder strBuder = new StringBuilder();
        strBuder.append("(");

        strBuder.append("date char(30), ");
        strBuder.append("open double(16,3), ");
        strBuder.append("high double(16,3), ");
        strBuder.append("low double(16,3), ");
        strBuder.append("close double(16,3), ");
        strBuder.append("volume int(10), ");
        strBuder.append("count int(10), ");
        strBuder.append("WAP double(16,3), ");
        strBuder.append("hasGaps double(16,3), ");
        strBuder.append("PRIMARY KEY(date)");

        strBuder.append(");");
        return strBuder.toString();
    }



}
