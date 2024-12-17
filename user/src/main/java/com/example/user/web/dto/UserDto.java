package com.example.user.web.dto;

import com.example.user.web.dto.mailbox.MailBoxDto;
import lombok.Data;

@Data
public class UserDto {
    private String login;
    private MailBoxDto mailBoxDto;
}
