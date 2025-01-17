package com.example.email.service;

import com.example.email.domain.Email;
import com.example.email.domain.MailBox;
import jakarta.mail.MessagingException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

public interface EmailService {

    Email sendEmail(Email email, String login, MultipartFile file)
            throws MessagingException, IOException;


    MailBox addEmailConfiguration(MailBox mailBox) throws MessagingException, IOException;

    @Transactional
    void refreshGmailTokens();
    String extractEmailDomain(String emailAddress);
}
