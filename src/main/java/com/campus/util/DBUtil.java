package com.campus.util;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * 数据库工具类。
 * 负责加载 db.properties 配置、获取数据库连接以及统一释放资源。
 */
public class DBUtil {

    private static String driver;
    private static String url;
    private static String username;
    private static String password;

    // 静态块：类加载时读取配置并注册驱动
    static {
        try (InputStream in = DBUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            Properties prop = new Properties();
            prop.load(in);
            driver = prop.getProperty("jdbc.driver");
            url = prop.getProperty("jdbc.url");
            username = prop.getProperty("jdbc.username");
            password = prop.getProperty("jdbc.password");
            Class.forName(driver);
        } catch (Exception e) {
            throw new ExceptionInInitializerError("数据库配置加载失败：" + e.getMessage());
        }
    }

    /** 获取一个数据库连接 */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    /** 统一关闭结果集、语句和连接 */
    public static void close(ResultSet rs, Statement st, Connection conn) {
        try { if (rs != null) rs.close(); } catch (SQLException ignored) {}
        try { if (st != null) st.close(); } catch (SQLException ignored) {}
        try { if (conn != null) conn.close(); } catch (SQLException ignored) {}
    }
}
