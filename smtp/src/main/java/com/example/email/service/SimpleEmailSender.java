package com.example.email.service;

import com.example.email.domain.Email;
import jakarta.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Component
@Slf4j
public class SimpleEmailSender extends EmailSenderTemplate {
    private final EmailService emailService;

    @Override
    protected void prepareEmail(Email email) throws UnsupportedEncodingException {
        log.info("Preparing simple email:");
        log.info("Subject: " + email.getSubject());
        log.info("Body: " + email.getBody());
    }

    @Override
    protected void sendEmail(Email email, String login, MultipartFile file) throws MessagingException, UnsupportedEncodingException {
        log.info("Sending simple email to: " + email.getRecipientEmail());
        emailService.sendEmail(email, login, file);
    }

    @Override
    protected void handleResponse(Email email) {
        log.info("Email sent successfully at: " + email.getSentAt());
    }
}
