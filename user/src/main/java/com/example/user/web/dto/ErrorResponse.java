package com.example.user.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class ErrorResponse {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private final LocalDateTime timeStamp;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private final String message;

    public ErrorResponse(String message) {
        timeStamp = LocalDateTime.now();
        this.message = message;
    }
}
