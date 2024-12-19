package com.example.email.exception;

import org.springframework.http.HttpStatus;

public class ServiceException extends RuntimeException {
    private final HttpStatus status;
    private final String errorBody;

    public ServiceException(HttpStatus status, String errorBody) {
        super("Error from downstream service: " + errorBody);
        this.status = status;
        this.errorBody = errorBody;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getErrorBody() {
        return errorBody;
    }
}

