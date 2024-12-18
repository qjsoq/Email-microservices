package com.example.email.service.impl;


import com.example.email.domain.Email;
import com.example.email.domain.MailBox;
import com.example.email.repository.MailBoxRepository;
import com.example.email.service.SendStrategy;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("ukr")
public class UkrNetSendStrategy implements SendStrategy {
    private final MailBoxRepository mailBoxRepository;

    private final Properties props;

    public UkrNetSendStrategy(MailBoxRepository mailBoxRepository, @Qualifier("ukr-properties") Properties props) {
        this.mailBoxRepository = mailBoxRepository;
        this.props = props;
    }

    @Override
    public Email sendWithStrategyEmail(Email email, MailBox mailBox)
            throws MessagingException, UnsupportedEncodingException {
        Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
            @Override
            protected jakarta.mail.PasswordAuthentication getPasswordAuthentication() {
                return new jakarta.mail.PasswordAuthentication(mailBox.getEmailAddress(),
                        mailBox.getAccessSmtp());
            }
        });

        session.setDebug(true);

        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(email.getSenderEmail(), mailBox.getUser().getLogin()));
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(email.getRecipientEmail()));
        msg.setSubject(email.getSubject());
        msg.setContent(email.getBody(), "text/html");

        Transport.send(msg);

        email.setSentAt(LocalDateTime.now());
        return email;
    }

}
