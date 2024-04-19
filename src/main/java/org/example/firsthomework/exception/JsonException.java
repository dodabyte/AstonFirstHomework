package org.example.firsthomework.exception;

import java.io.IOException;

public class JsonException extends IOException {
    public JsonException() { super(); }

    public JsonException(String message) {
        super(message);
    }

    public JsonException(Exception exception) { super(exception); }
}
