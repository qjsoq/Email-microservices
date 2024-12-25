package com.example.email.service.impl;

import com.example.email.domain.Email;
import com.example.email.domain.MailBox;
import com.example.email.exception.SendException;
import com.example.email.service.SendStrategy;
import com.sun.mail.smtp.SMTPTransport;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("gmail")
public class GmailSendStrategy implements SendStrategy {
    private static final String CONTENT_TYPE_HTML = "text/html";
    private static final String XOAUTH2_AUTH_FORMAT = "user=%s\u0001auth=Bearer %s\u0001\u0001";
    private static final String SMTP_HOST = "smtp.gmail.com";

    private final Properties props;

    public GmailSendStrategy(
            @Qualifier("gmail-properties") Properties props) {
        this.props = props;
    }

    @Override
    public Email sendWithStrategyEmail(Email email, MailBox mailBox) {
        Session session = createSession();
        session.setDebug(true);

        try {
            MimeMessage msg = createMessage(session, email, mailBox);
            sendEmailWithOAuth2(session, email, mailBox, msg);
            email.setSentAt(LocalDateTime.now());
            return email;

        } catch (MessagingException | IOException e) {
            throw new SendException("Failed to send email: " + e.getMessage());
        }
    }

    @Override
    public void checkIsPasswordCorrect(MailBox mailBox) {
        configureXOAuth2();

        try (Transport transport = createSession().getTransport()) {
            transport.connect(mailBox.getEmailAddress(), mailBox.getAccessSmtp());
        } catch (MessagingException e) {
            throw new SendException(
                    "Invalid credentials for Gmail account: " + mailBox.getEmailAddress());
        } finally {
            removeXOAuth2Configuration();
        }
    }


    private Session createSession() {
        return Session.getInstance(props);
    }


    private void sendEmailWithOAuth2(Session session, Email email, MailBox mailBox, MimeMessage msg)
            throws MessagingException {
        try (SMTPTransport transport = new SMTPTransport(session, null)) {
            String authString = String.format(XOAUTH2_AUTH_FORMAT, email.getSenderEmail(),
                    mailBox.getAccessSmtp());
            String encodedAuthString =
                    Base64.getEncoder().encodeToString(authString.getBytes(StandardCharsets.UTF_8));

            transport.connect(SMTP_HOST, email.getSenderEmail(), null);
            transport.issueCommand("AUTH XOAUTH2 " + encodedAuthString, 235);
            transport.sendMessage(msg, msg.getAllRecipients());
        }
    }


    private void configureXOAuth2() {
        props.put("mail.smtp.auth.mechanisms", "XOAUTH2");
        props.put("mail.debug.auth", "true");
        props.put("mail.smtp.host", SMTP_HOST);
    }


    private void removeXOAuth2Configuration() {
        props.remove("mail.smtp.auth.mechanisms");
        props.remove("mail.debug.auth");
        props.remove("mail.smtp.host");
    }

    private MimeMessage createMessage(Session session, Email email, MailBox mailBox)
            throws MessagingException, UnsupportedEncodingException {
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(email.getSenderEmail(), mailBox.getUser().getLogin()));
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(email.getRecipientEmail()));
        msg.setSubject(email.getSubject());
        msg.setContent(email.getBody(), CONTENT_TYPE_HTML);
        return msg;
    }
}
