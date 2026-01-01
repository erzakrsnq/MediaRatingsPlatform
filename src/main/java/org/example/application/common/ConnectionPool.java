package org.example.application.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionPool {

    private String dbType;
    private String host;
    private int port;
    private String user;
    private String password;
    private String dbName;
    private String jdbcConnectionUrl;

    public ConnectionPool(String dbType, String host, int port, String user, String password, String dbName) {
        this.dbType = dbType;
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        this.dbName = dbName;

        this.jdbcConnectionUrl
                = "jdbc:%s://%s:%s/%s"
                .formatted(dbType, host, port, dbName);
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcConnectionUrl, user, password);
    }
}

