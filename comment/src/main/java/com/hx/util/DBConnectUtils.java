//package com.hx.util;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.jdbc.datasource.DriverManagerDataSource;
//
//import java.sql.*;
//import java.util.Properties;
//
//
//public class DBConnectUtils {
//	private static final Logger logger = LoggerFactory.getLogger(DBConnectUtils.class);
//
//
//    //创建Oracle数据库连接---sid
//    public static DriverManagerDataSource getOracleDataSource(String ip, String port, String instance, String user, String pwd) throws Exception {
//        String driver = "oracle.jdbc.driver.OracleDriver";
//        String url = String.format("jdbc:oracle:thin:@//%s:%s/%s",ip,port,instance);
//        return getDataSource(driver,url, user, pwd);
//    }
//
//    public static DriverManagerDataSource getDataSource(String driver,String url,
//                                                        String user,String password) throws Exception {
//        Class.forName(driver);
//        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource(url, user, password);
//        Properties properties = new Properties();
//        //配置datasource
//        properties.setProperty("maxActive", "10");
//        properties.setProperty("minIdle", "7");
//        driverManagerDataSource.setConnectionProperties(properties);
//        return driverManagerDataSource;
//    }
//
//
//
//    public static Connection getMysqlConn(String ip, String port, String instance, String user, String pwd) throws Exception {
//        String driver = "com.mysql.jdbc.Driver";
//        String url = String.format("jdbc:mysql://%s:%s/%s",ip,port,instance);
//        return getConnection(driver,url, user, pwd);
//    }
//    //创建Oracle数据库连接
//    public static Connection getOracleConn(String ip, String port, String instance, String user, String pwd) throws Exception {
//        String driver = "oracle.jdbc.driver.OracleDriver";
//        String url = String.format("jdbc:oracle:thin:@//%s:%s/%s",ip,port,instance);
//        return getConnection(driver,url, user, pwd);
//    }
//    public static Connection getConnection(String driver,String url,
//            String user,String password) throws Exception {
//        Class.forName(driver);
//        Connection conn = DriverManager.getConnection(url, user, password);
//        return conn;
//    }
//    
//    // 关闭资源的标准步骤
//    public static void closeAllResource(Connection conn, Statement stat, ResultSet ... res) {
//        try {
//            for (ResultSet resultSet : res) {
//                if (resultSet != null) {
//                    resultSet.close();
//                }
//            }
//        } catch (SQLException e) {
//        	logger.error(e.toString());
//        } finally {
//            try {
//                if (stat != null) {
//                    stat.close();
//                }
//            } catch (SQLException e) {
//            	logger.error(e.toString());
//            } finally {
//                stat = null;
//                try {
//                    if (conn != null) {
//                        conn.close();
//                    }
//                } catch (SQLException e) {
//                	logger.error(e.toString());
//                } finally {
//                    conn = null;
//                }
//            }
//        }
//    }
//
//    // 关闭数据库连接
//    public static void closeConn(Connection conn) {
//        if (conn != null) {
//            try {
//                conn.close();
//            } catch (SQLException e) {
//            	logger.error(e.toString());
//            } finally {
//                conn = null;
//            }
//        }
//    }
//}
