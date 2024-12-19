package com.example.imap.web;

import com.example.imap.exception.InvalidEmailReaderException;
import com.example.imap.exception.PropertiesNotFoundException;
import com.example.imap.exception.ReadException;
import com.example.imap.exception.ServiceException;
import com.example.imap.web.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ImapExceptionHandler {
    @ExceptionHandler({PropertiesNotFoundException.class})
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ErrorResponse notFoundExceptionHandler(RuntimeException runtimeException) {
        return new ErrorResponse(runtimeException.getMessage());
    }
    @ExceptionHandler({InvalidEmailReaderException.class, ServiceException.class, ReadException.class})
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ErrorResponse invalidParametersExceptionHandler(RuntimeException runtimeException) {
        return new ErrorResponse(runtimeException.getLocalizedMessage());
    }
}
