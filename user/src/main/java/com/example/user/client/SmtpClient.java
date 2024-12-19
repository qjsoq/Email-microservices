package com.example.user.client;

import com.example.user.domain.MailBox;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(value = "smtp-service", url = "http://localhost:8083")
public interface SmtpClient {

    @PostMapping("/api/v1/email/config")
    MailBox addAccount(@RequestBody MailBox mailBox);
}
