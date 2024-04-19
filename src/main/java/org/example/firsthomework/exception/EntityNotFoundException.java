package org.example.firsthomework.exception;

public class EntityNotFoundException extends Exception {
    public EntityNotFoundException(Exception exception) {
        super(exception);
    }

    public EntityNotFoundException() {
        super();
    }

    public EntityNotFoundException(String message) {
        super(message);
    }
}
