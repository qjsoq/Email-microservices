package com.example.user.web.dto.mailbox;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MailBoxCreation {
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Provide valid email")
    private String emailAddress;
    @NotBlank(message = "Specify password")
    @Size(min = 5, message = "Enter at least 6 characters")
    private String accessSmtp;
}
