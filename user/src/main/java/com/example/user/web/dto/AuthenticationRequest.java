package com.example.user.web.dto;

import lombok.Data;

@Data
public class AuthenticationRequest {

    String email;
    String password;
}
