package com.example.email.web;

import com.example.email.exception.InvalidEmailDomainException;
import com.example.email.exception.MailBoxAlreadyExistsException;
import com.example.email.exception.MailboxNotFoundException;
import com.example.email.exception.SendException;
import com.example.email.exception.ServiceException;
import com.example.email.exception.StrategyNotFoundException;
import com.example.email.web.dto.ErrorResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SmtpExceptionHandler {
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<Map<String, List<String>>> argumentNotValid(
            MethodArgumentNotValidException exception) {
        Map<String, List<String>> errors = exception.getFieldErrors()
                .stream()
                .collect(Collectors.groupingBy(FieldError::getField,
                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())));
        return ResponseEntity.badRequest().body(errors);
    }
    @ExceptionHandler({MailboxNotFoundException.class, StrategyNotFoundException.class})
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ErrorResponse notFoundExceptionHandler(RuntimeException runtimeException) {
        return new ErrorResponse(runtimeException.getMessage());
    }
    @ExceptionHandler({MailBoxAlreadyExistsException.class, InvalidEmailDomainException.class, SendException.class, ServiceException.class})
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ErrorResponse invalidParametersExceptionHandler(RuntimeException runtimeException) {
        return new ErrorResponse(runtimeException.getLocalizedMessage());
    }
}
