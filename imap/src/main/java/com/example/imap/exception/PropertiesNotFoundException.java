package com.example.imap.exception;

public class PropertiesNotFoundException extends RuntimeException {
    public PropertiesNotFoundException(String message) {
        super(message);
    }
}
