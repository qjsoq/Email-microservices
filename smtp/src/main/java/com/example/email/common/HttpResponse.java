package com.example.email.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class HttpResponse {
    protected String timeStamp;
    protected int code;
    protected HttpStatus httpStatus;
    protected String message;
    protected String path;

    @NoArgsConstructor
    public static class HttpResponseBuilder {
        private String timeStamp;
        private int code;
        private HttpStatus httpStatus;
        private String message;
        private String path;


        public HttpResponseBuilder withTimeStamp(String timeStamp) {
            this.timeStamp = timeStamp;
            return this;
        }

        public HttpResponseBuilder withCode(int code) {
            this.code = code;
            return this;
        }

        public HttpResponseBuilder withHttpStatus(HttpStatus httpStatus) {
            this.httpStatus = httpStatus;
            return this;
        }

        public HttpResponseBuilder withMessage(String message) {
            this.message = message;
            return this;
        }

        public HttpResponseBuilder withPath(String path) {
            this.path = path;
            return this;
        }

        public HttpResponse build() {
            HttpResponse response = new HttpResponse();
            response.timeStamp = this.timeStamp;
            response.code = this.code;
            response.httpStatus = this.httpStatus;
            response.message = this.message;
            response.path = this.path;
            return response;
        }
    }
}
