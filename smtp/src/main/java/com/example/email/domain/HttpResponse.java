package com.example.email.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class HttpResponse {
    protected String timeStamp;
    protected int code;
    protected HttpStatus httpStatus;
    protected String message;
    protected String customMessage;
    protected String path;
    protected String requestMethod;
    protected Map<?, ?> data;
}
