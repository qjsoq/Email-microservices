package com.example.imap.web.dto;

import lombok.Data;

@Data
public class MailBoxDto {
    private String[] folders;
    private String emailAddress;
}
