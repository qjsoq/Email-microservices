package com.example.user.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthenticationRequest {
    @NotBlank(message = "Login cannot be blank")
    String login;
    @NotBlank(message = "Specify password")
    @Size(min = 5, max = 32, message = "Enter at least 5 and less than 32 characters")
    String password;
}
