package com.ngo.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DBUtil {
    private static final HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/ngo_db?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8");
        config.setUsername("root");
        config.setPassword("password"); // Change to your DB password
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");

        // Pool tuning
        config.setMaximumPoolSize(15);
        config.setMinimumIdle(5);
        config.setIdleTimeout(300000);       // 5 minutes
        config.setConnectionTimeout(20000);   // 20 seconds
        config.setMaxLifetime(1200000);       // 20 minutes
        config.setLeakDetectionThreshold(60000);

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close(); // Returns to pool, doesn't actually close
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
