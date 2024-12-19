package com.example.user.client;

import com.example.user.domain.MailBox;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;

public interface SmtpClient {

    @PostExchange("/api/v1/email/config")
    MailBox addAccount(@RequestBody MailBox mailBox);
}
