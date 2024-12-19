package com.example.email.exception;

public class MailBoxAlreadyExistsException extends RuntimeException {
    public MailBoxAlreadyExistsException(String message) {
        super(message);
    }
}
