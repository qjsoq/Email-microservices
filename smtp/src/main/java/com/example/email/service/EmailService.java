package com.example.email.service;

import com.example.email.domain.Email;
import com.example.email.domain.MailBox;
import jakarta.mail.MessagingException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.springframework.transaction.annotation.Transactional;

public interface EmailService {

    Email sendEmail(Email email, String login)
            throws MessagingException, UnsupportedEncodingException;


    MailBox addEmailConfiguration(MailBox mailBox) throws MessagingException, IOException;

    @Transactional
    void refreshGmailTokens();
}
