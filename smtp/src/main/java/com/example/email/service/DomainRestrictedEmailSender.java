package com.example.email.service;

import com.example.email.domain.Email;
import com.example.email.util.EmailConfiguration;
import jakarta.mail.MessagingException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Component
@Slf4j
public class DomainRestrictedEmailSender extends EmailSenderTemplate {
    private final EmailService emailService;
    @Override
    protected void prepareEmail(Email email) throws UnsupportedEncodingException {
        log.info("Preparing email for domain validation...");
        String senderDomain = emailService.extractEmailDomain(email.getSenderEmail());
        boolean isValidDomain = Arrays.stream(EmailConfiguration.values())
                .anyMatch(config -> config.getDomainName().equalsIgnoreCase(senderDomain));
        if (!isValidDomain) {
            throw new UnsupportedEncodingException("Sender email domain is not allowed: " + senderDomain);
        }

        log.info("Email passed domain validation. Sender: " + email.getSenderEmail());
    }
    @Override
    protected void sendEmail(Email email, String login, MultipartFile file)
            throws MessagingException, IOException {
        log.info("Sending domain-restricted email to: " + email.getRecipientEmail());
        emailService.sendEmail(email, login, file);
    }
    @Override
    protected void handleResponse(Email email) {
        log.info("Domain-restricted email sent successfully at: " + email.getSentAt());
    }
}
