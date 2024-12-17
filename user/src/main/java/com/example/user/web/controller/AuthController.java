package com.example.user.web.controller;


import com.example.user.common.HttpResponse;
import com.example.user.domain.User;
import com.example.user.service.UserService;
import com.example.user.web.dto.UserCreationDto;
import com.example.user.web.mapper.UserMapper;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    public ResponseEntity<HttpResponse> createUser(@RequestBody UserCreationDto userCreationDto) {
        User newUser = userService.saveUser(userMapper.toUser(userCreationDto));
        return ResponseEntity.created(URI.create("")).body(
                HttpResponse.builder()
                        .code(201)
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("user", newUser))
                        .message("User created ")
                        .httpStatus(HttpStatus.CREATED)
                        .build());
    }
}
