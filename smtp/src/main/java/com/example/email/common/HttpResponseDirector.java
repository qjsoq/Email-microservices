package com.example.email.common;

public class HttpResponseDirector {
    private Builder builder;

    public HttpResponseDirector(Builder builder) {
        this.builder = builder;
    }

    public void setBuilder(Builder builder) {
        this.builder = builder;
    }

    public HttpResponse constructResponse() {
        return builder.build();
    }
}
