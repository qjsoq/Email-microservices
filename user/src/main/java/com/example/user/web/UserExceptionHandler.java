package com.example.user.web;

import com.example.user.exception.InvalidPasswordException;
import com.example.user.exception.InvalidTokenException;
import com.example.user.exception.ServiceException;
import com.example.user.exception.UserAlreadyExistsException;
import com.example.user.exception.UserNotFoundException;
import com.example.user.web.dto.ErrorResponse;
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
public class UserExceptionHandler {
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<Map<String, List<String>>> argumentNotValid(
            MethodArgumentNotValidException exception) {
        Map<String, List<String>> errors = exception.getFieldErrors()
                .stream()
                .collect(Collectors.groupingBy(FieldError::getField,
                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())));
        return ResponseEntity.badRequest().body(errors);
    }
    @ExceptionHandler({UserNotFoundException.class})
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ErrorResponse notFoundExceptionHandler(RuntimeException runtimeException) {
        return new ErrorResponse(runtimeException.getMessage());
    }
    @ExceptionHandler({InvalidPasswordException.class, UserAlreadyExistsException.class,
            InvalidTokenException.class, ServiceException.class})
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ErrorResponse invalidParametersExceptionHandler(RuntimeException runtimeException) {
        return new ErrorResponse(runtimeException.getLocalizedMessage());
    }
}
