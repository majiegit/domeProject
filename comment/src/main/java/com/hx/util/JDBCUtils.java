package com.hx.util;

import java.sql.*;

/**
 * jdbc操作工具类
 * @author  lyf
 */
public class JDBCUtils {
    private JDBCUtils(){}
    /**
     * 提供静态的getConnection方法，封装第1,2
     */
    public static Connection getConnection(String driverClass,String url,String user,String password){
        // 封装第1,2
        try {
            // 1,注册驱动
            Class.forName(driverClass);
            // 2,获取数据库连接
            Connection conn = DriverManager.getConnection(url, user, password);
            return conn;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     *   提供静态的close方法，封装第6
     */
    public static void close(Connection conn, Statement st, ResultSet rs){
        //防止空指针
        if(conn!=null){
            try {
                conn.close();//释放连接资源
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        //防止空指针
        if(st!=null){
            try {
                st.close();//释放传输器资源
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(rs!=null){
            try {
                rs.close();//释放结果集资源
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
