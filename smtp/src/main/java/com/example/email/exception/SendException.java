package com.example.email.exception;

public class SendException extends RuntimeException {
    public SendException(String message) {
        super(message);
    }
}
