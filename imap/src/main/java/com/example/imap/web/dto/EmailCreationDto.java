package com.example.imap.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmailCreationDto {
    @NotBlank(message = "Sender email cannot be blank")
    @Email(message = "Provide valid email")
    private String senderEmail;
    @NotBlank(message = "Recipient email cannot be blank")
    @Email(message = "Provide valid email")
    private String recipientEmail;
    @NotBlank(message = "Subject cannot be blank")
    private String subject;
    @NotBlank(message = "Body cannot be blank")
    private String body;
}
