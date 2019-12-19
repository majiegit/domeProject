package com.hx.util;

import ch.ethz.ssh2.Connection;

import java.io.IOException;

public class ConnUtil {
    /**
     * 获取连接
     *
     * @param ip
     * @param userName
     * @param pwd
     * @param port
     * @return
     */
    public static Connection getConn(String ip, String userName, String pwd, int port) {
        Connection conn = new Connection(ip);
        boolean blag = false;
        try {
            conn.connect();
            boolean isAuthenticated = conn.authenticateWithPassword(userName, pwd);
            if (isAuthenticated) {
                blag = true;
            }
            if (isAuthenticated == false) {
                throw new IOException("Authentication failed.文件scp到数据服务器时发生异常");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (blag) {
            return conn;
        } else {
            return null;
        }
    }
}
