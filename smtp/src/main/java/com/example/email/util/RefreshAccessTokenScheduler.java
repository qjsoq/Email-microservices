package com.example.email.util;

import com.example.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefreshAccessTokenScheduler {
    private final EmailService emailService;

    @Scheduled(cron = "0 0 * * * *")
    public void refreshGmailTokens() {
        System.out.println("Refreshing tokens");
        emailService.refreshGmailTokens();
    }
}
