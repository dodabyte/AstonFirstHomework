package org.example.firsthomework.session;

import org.example.firsthomework.exception.DatabaseDriverLoadException;
import org.example.firsthomework.exception.SessionManagerException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

public class SessionManagerImpl implements SessionManager {
    private static final String PROPERTY_FILE_PATH_DEFAULT = "connection/connection.properties";
    private static final int TIMEOUT_IN_SECONDS = 10;
    private Connection connection;
    private static SessionManager instance;

    private final String url;
    private final String username;
    private final String password;

    private SessionManagerImpl(PropertyFile propertyFile) {
        instance = this;
        if (propertyFile == null) throw new SessionManagerException("Property File not loaded.");
        url = propertyFile.getValue("url");
        username = propertyFile.getValue("username");
        password = propertyFile.getValue("password");
        try {
            Class.forName(propertyFile.getValue("driver"));
        } catch (ClassNotFoundException e) {
            throw new DatabaseDriverLoadException("Database driver not loaded.");
        }
    }

    private SessionManagerImpl(String url, String username, String password, String driver) {
        instance = this;
        this.url = url;
        this.username = username;
        this.password = password;
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new DatabaseDriverLoadException("Database driver not loaded.");
        }
    }

    public static synchronized SessionManager getInstance() {
        return getInstance(null);
    }

    public static synchronized SessionManager getInstance(PropertyFile propertyFile) {
        if (instance == null) {
            instance = new SessionManagerImpl(Objects.requireNonNullElseGet(propertyFile,
                    () -> new PropertyFile(PROPERTY_FILE_PATH_DEFAULT)));
        }
        return instance;
    }

    public static synchronized SessionManager getInstance(String url, String username, String password, String driver) {
        if (instance == null) {
            instance = new SessionManagerImpl(url, username, password, driver);
        }
        return instance;
    }

    private void checkConnection() {
        try {
            if (connection == null || !connection.isValid(TIMEOUT_IN_SECONDS)) {
                throw new SessionManagerException("Connection is invalid.");
            }
        } catch (SQLException ex) {
            throw new SessionManagerException(ex);
        }
    }

    @Override
    public void begin() {
        try {
            connection = DriverManager.getConnection(
                    url,
                    username,
                    password);
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new SessionManagerException(e);
        }
    }

    @Override
    public void commit() {
        checkConnection();
        try {
            connection.commit();
        } catch (SQLException e) {
            throw new SessionManagerException(e);
        }
    }

    @Override
    public void rollback() {
        checkConnection();
        try {
            connection.rollback();
        } catch (SQLException e) {
            throw new SessionManagerException(e);
        }
    }

    @Override
    public void close() {
        checkConnection();
        try {
            connection.close();
        } catch (SQLException e) {
            throw new SessionManagerException(e);
        }
    }

    @Override
    public Connection getSession() {
        checkConnection();
        return connection;
    }
}
