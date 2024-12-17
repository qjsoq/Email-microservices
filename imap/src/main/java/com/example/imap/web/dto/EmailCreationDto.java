package com.example.imap.web.dto;

import lombok.Data;

@Data
public class EmailCreationDto {
    private String senderEmail;
    private String recipientEmail;
    private String subject;
    private String body;
}
