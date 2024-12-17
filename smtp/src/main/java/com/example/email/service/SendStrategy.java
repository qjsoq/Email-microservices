package com.example.email.service;


import com.example.email.domain.Email;
import jakarta.mail.MessagingException;
import java.io.UnsupportedEncodingException;

public interface SendStrategy {
    Email sendWithStrategyEmail(Email email)
            throws MessagingException, UnsupportedEncodingException;
}
