package com.example.email.service.impl;


import com.example.email.domain.Email;
import com.example.email.domain.MailBox;
import com.example.email.repository.MailBoxRepository;
import com.example.email.service.SendStrategy;
import com.sun.mail.smtp.SMTPTransport;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("gmail")
public class GmailSendStrategy implements SendStrategy {
    private final MailBoxRepository mailBoxRepository;
    private final Properties props;

    public GmailSendStrategy(MailBoxRepository mailBoxRepository, @Qualifier("gmail-properties") Properties props) {
        this.mailBoxRepository = mailBoxRepository;
        this.props = props;
    }

    @Override
    public Email sendWithStrategyEmail(Email email, MailBox mailBox) throws MessagingException, UnsupportedEncodingException {
        var configs = mailBox.getEmailConfiguration();

        // Initialize the JavaMail session
        Session session = Session.getInstance(props);
        session.setDebug(true);

        // Create the email message
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(email.getSenderEmail(), mailBox.getUser().getLogin()));
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(email.getRecipientEmail()));
        msg.setSubject(email.getSubject());
        msg.setContent(email.getBody(), "text/html");

        try (SMTPTransport transport = new SMTPTransport(session, null)) {
            transport.connect(configs.getHost(), email.getSenderEmail(), null);

            // Build the OAuth2 AUTH command
            String authString = String.format("user=%s\u0001auth=Bearer %s\u0001\u0001",
                    email.getSenderEmail(),
                    mailBox.getAccessSmtp());
            String encodedAuthString = Base64.getEncoder().encodeToString(authString.getBytes(
                    StandardCharsets.UTF_8));

            // Issue the AUTH command
            transport.issueCommand("AUTH XOAUTH2 " + encodedAuthString, 235);

            // Send the message
            transport.sendMessage(msg, msg.getAllRecipients());
        } catch (MessagingException exception) {
            throw exception;
        }

        // Update and return the email object
        email.setSentAt(LocalDateTime.now());
        return email;
    }

}
