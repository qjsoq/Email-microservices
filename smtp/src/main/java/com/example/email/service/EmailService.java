package com.example.email.service;

import com.example.email.domain.Email;
import com.example.email.domain.MailBox;
import com.example.email.web.dto.DetailedReceivedEmail;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public interface EmailService {

    Email sendEmail(Email email, String login) throws MessagingException, UnsupportedEncodingException;


    MailBox addEmailConfiguration(MailBox mailBox) throws MessagingException;
}
