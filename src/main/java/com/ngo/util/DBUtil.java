package com.ngo.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DBUtil {
    private static final HikariDataSource dataSource;

    private static final String DEFAULT_JDBC_URL =
            "jdbc:mysql://localhost:3306/ngo_db?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8";
    private static final String DEFAULT_USERNAME = "root";

    static {
        String jdbcUrl = getenvOrNull("NGO_DB_URL");
        String username = getenvOrNull("NGO_DB_USER");
        String password = getenvOrNull("NGO_DB_PASSWORD");

        if (jdbcUrl == null || username == null || password == null) {
            Properties fileProps = loadDbProperties();
            if (jdbcUrl == null) {
                jdbcUrl = fileProps.getProperty("jdbc.url", DEFAULT_JDBC_URL);
            }
            if (username == null) {
                username = fileProps.getProperty("jdbc.username", DEFAULT_USERNAME);
            }
            if (password == null) {
                password = fileProps.getProperty("jdbc.password", "");
            }
        }

        if (password == null || password.isEmpty()) {
            System.err.println(
                    "[Donum] Database password is not set. Copy src/main/resources/db.properties.example "
                            + "to src/main/resources/db.properties and set jdbc.password, "
                            + "or set NGO_DB_PASSWORD. Login and all DB features will fail until this is fixed.");
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password != null ? password : "");
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");

        config.setMaximumPoolSize(15);
        config.setMinimumIdle(5);
        config.setIdleTimeout(300000);
        config.setConnectionTimeout(20000);
        config.setMaxLifetime(1200000);
        config.setLeakDetectionThreshold(60000);

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        dataSource = new HikariDataSource(config);
    }

    private static String getenvOrNull(String name) {
        String value = System.getenv(name);
        return (value == null || value.trim().isEmpty()) ? null : value.trim();
    }

    private static Properties loadDbProperties() {
        Properties props = new Properties();
        try (InputStream in = DBUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load db.properties", e);
        }
        return props;
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
