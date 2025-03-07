package com.example.email.service;

import com.example.email.domain.Email;
import jakarta.mail.MessagingException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.springframework.web.multipart.MultipartFile;

public abstract class EmailSenderTemplate {
    public final void sendEmailTemplate(Email email, String login, MultipartFile file)
            throws IOException,
            MessagingException {
        prepareEmail(email);
        sendEmail(email, login, file);
        handleResponse(email);
    }

    protected abstract void prepareEmail(Email email) throws UnsupportedEncodingException;

    protected abstract void sendEmail(Email email, String login, MultipartFile file)
            throws MessagingException, IOException;

    protected abstract void handleResponse(Email email);
}