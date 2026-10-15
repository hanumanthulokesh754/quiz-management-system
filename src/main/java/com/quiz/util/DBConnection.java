package com.quiz.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Creates JDBC connections. Settings come from environment variables (DB_URL, DB_USER, DB_PASSWORD)
 * when they are set (cloud), otherwise from src/main/resources/db.properties (local).
 */
public final class DBConnection {

    private static String url;
    private static String username;
    private static String password;

    static {
        try (InputStream in = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in == null) {
                throw new IllegalStateException("db.properties not found on the classpath");
            }
            Properties props = new Properties();
            props.load(in);
            url = env("DB_URL", props.getProperty("db.url"));
            username = env("DB_USER", props.getProperty("db.username"));
            password = env("DB_PASSWORD", props.getProperty("db.password"));
            Class.forName(props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver"));
        } catch (IOException | ClassNotFoundException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private DBConnection() { }

    /** Returns the environment variable if it is set and not blank, otherwise the fallback. */
    private static String env(String name, String fallback) {
        String v = System.getenv(name);
        return (v == null || v.isBlank()) ? fallback : v;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}