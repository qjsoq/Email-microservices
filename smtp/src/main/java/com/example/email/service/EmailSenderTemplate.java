package com.example.email.service;

import com.example.email.domain.Email;
import jakarta.mail.MessagingException;
import java.io.UnsupportedEncodingException;

public abstract class EmailSenderTemplate {
    public final void sendEmailTemplate(Email email, String login) throws UnsupportedEncodingException,
            MessagingException {
        prepareEmail(email);
        sendEmail(email, login);
        handleResponse(email);
    }

    protected abstract void prepareEmail(Email email) throws UnsupportedEncodingException;

    protected abstract void sendEmail(Email email, String login)
            throws MessagingException, UnsupportedEncodingException;

    protected abstract void handleResponse(Email email);
}