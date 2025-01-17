package com.example.email.service;


import com.example.email.domain.Email;
import com.example.email.domain.MailBox;
import jakarta.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import org.springframework.web.multipart.MultipartFile;

public interface SendStrategy {
    Email sendWithStrategyEmail(Email email, MailBox mailBox, MultipartFile file)
            throws MessagingException, UnsupportedEncodingException;

    void checkIsPasswordCorrect(MailBox mailBox) throws MessagingException;

}
