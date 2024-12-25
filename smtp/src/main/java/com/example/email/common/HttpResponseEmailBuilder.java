package com.example.email.common;

import org.springframework.http.HttpStatus;

import java.time.Instant;

public class HttpResponseEmailBuilder implements Builder {
    private final HttpResponse.HttpResponseBuilder responseBuilder;

    public HttpResponseEmailBuilder() {
        this.responseBuilder = new HttpResponse.HttpResponseBuilder();
    }

    @Override
    public HttpResponse build() {
        return responseBuilder
                .withTimeStamp(Instant.now().toString())
                .withCode(200)
                .withHttpStatus(HttpStatus.OK)
                .withMessage("Email sent successfully")
                .withPath("/api/v1/emails")
                .build();
    }
}
