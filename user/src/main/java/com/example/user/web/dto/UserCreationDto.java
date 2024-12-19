package com.example.user.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserCreationDto {
    @NotBlank(message = "Login cannot be empty")
    @Pattern(regexp = "^\\w+$", message = "You can use a-z, 0-9 and underscores")
    @Size(min = 2, max = 32, message = "Login must contain at least 2 characters, and no more than 32 characters")
    private String login;
    @NotBlank(message = "Specify password")
    @Size(min = 5, max = 32, message = "Enter at least 5 and less than 32 characters")
    private String password;
}
