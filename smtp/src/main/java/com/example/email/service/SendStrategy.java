package com.example.email.service;


import com.example.email.domain.Email;
import com.example.email.domain.MailBox;
import jakarta.mail.MessagingException;
import java.io.UnsupportedEncodingException;

public interface SendStrategy {
    Email sendWithStrategyEmail(Email email, MailBox mailBox)
            throws MessagingException, UnsupportedEncodingException;

    void checkIsPasswordCorrect(MailBox mailBox) throws MessagingException;

}
