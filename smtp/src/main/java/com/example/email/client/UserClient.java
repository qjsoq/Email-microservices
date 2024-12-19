package com.example.email.client;

import com.example.email.domain.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "user-service", url = "http://localhost:8082")
public interface UserClient {
    @RequestMapping(method = RequestMethod.GET, value = "api/v1/auth/validate-token/{token}")
    User checkUser(@PathVariable String token);
}
