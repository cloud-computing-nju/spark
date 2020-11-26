package db;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DbHelper {

    public static Connection getConnection() throws IOException, ClassNotFoundException, SQLException {
        Properties properties=new Properties();
        properties.load(DbHelper.class.getClassLoader().getResourceAsStream("dbinfo.properties"));
        String driver=properties.getProperty("driver");
        String url=properties.getProperty("url");
        String userName=properties.getProperty("userName");
        String password=properties.getProperty("password");
        Class.forName(driver);

        return DriverManager.getConnection(url, userName, password);
    }

    public static void callProc( Connection conn,String sql,String[] parameters) throws SQLException {
        try {
            CallableStatement cs =conn.prepareCall(sql);
            //给？赋值
            if(parameters != null) {
                for (int i =0; i < parameters.length; i++)
                    cs.setObject(i + 1, parameters[i]);
            }
            cs.execute();
        } catch(Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        } finally{
            //关闭连接
            conn.close();
        }
    }
}
