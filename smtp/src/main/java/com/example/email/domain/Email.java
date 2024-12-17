package com.example.email.domain;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Email {
    private String subject;
    private String senderEmail;
    private String recipientEmail;
    private String body;
    private LocalDateTime sentAt;
}
