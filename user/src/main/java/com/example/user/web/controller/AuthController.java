package com.example.user.web.controller;


import com.example.user.common.HttpResponse;
import com.example.user.domain.User;
import com.example.user.service.UserService;
import com.example.user.web.dto.AuthenticationRequest;
import com.example.user.web.dto.AuthenticationResponse;
import com.example.user.web.dto.UserCreationDto;
import com.example.user.web.mapper.AuthenticationMapper;
import com.example.user.web.mapper.UserMapper;
import jakarta.validation.Valid;
import java.io.IOException;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final AuthenticationMapper authenticationMapper;

    @PostMapping
    public ResponseEntity<HttpResponse> createUser(
            @Valid @RequestBody UserCreationDto userCreationDto)
            throws IOException {
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

    @PostMapping("/sign-in")
    public ResponseEntity<AuthenticationResponse> signIn(
            @Valid @RequestBody AuthenticationRequest request) {
        return ResponseEntity.of(userService
                .signIn(request.getLogin(), request.getPassword())
                .map(authenticationMapper::toAuthResponse));
    }

    @GetMapping("/validate-token/{token}")
    public ResponseEntity<User> validateToken(@PathVariable String token) {
        return ResponseEntity.ok(userService.validateToken(token));
    }

    @GetMapping("")
    public String handleGoogleCallback(@RequestParam("code") String authorizationCode)
            throws URISyntaxException, IOException {
        return "Access Token: " + authorizationCode;
    }
}
