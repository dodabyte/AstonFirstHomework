package org.example.firsthomework.exception;

import java.sql.SQLException;

public class DataAccessObjectException extends RuntimeException {
    public DataAccessObjectException(String message) {
        super(message);
    }

    public DataAccessObjectException(SQLException exception) {
        super(exception);
    }
}
