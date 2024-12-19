package com.example.email.client;

import com.example.email.domain.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

public interface UserClient {
    @GetExchange("api/v1/auth/validate-token/{token}")
    User checkUser(@PathVariable String token);
}
