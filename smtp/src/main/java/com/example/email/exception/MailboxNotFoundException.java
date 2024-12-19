package com.example.email.exception;

public class MailboxNotFoundException extends RuntimeException {
    public MailboxNotFoundException(String message) {
        super(message);
    }
}
