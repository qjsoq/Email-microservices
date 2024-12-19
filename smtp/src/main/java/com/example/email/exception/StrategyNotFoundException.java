package com.example.email.exception;

public class StrategyNotFoundException extends RuntimeException {
    public StrategyNotFoundException(String message) {
        super(message);
    }
}
