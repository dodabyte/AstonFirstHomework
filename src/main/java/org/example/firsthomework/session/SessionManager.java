package org.example.firsthomework.session;

import java.sql.Connection;

public interface SessionManager extends AutoCloseable {
    void begin();

    void commit();

    void rollback();

    void close();

    Connection getSession();
}
