package com.example.imap.web.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class EmailDto {
    private String subject;
    private LocalDateTime sentAt;
}
