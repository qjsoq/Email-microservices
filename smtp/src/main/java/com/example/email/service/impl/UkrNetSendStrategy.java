package com.example.email.service.impl;

import com.example.email.domain.Email;
import com.example.email.domain.MailBox;
import com.example.email.exception.SendException;
import com.example.email.service.SendStrategy;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.AuthenticationFailedException;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

;

@Service("ukr")
public class UkrNetSendStrategy implements SendStrategy {
    private static final String CONTENT_TYPE_HTML = "text/html";
    private static final String PROTOCOL_SMTP = "smtp";
    private final Properties props;

    public UkrNetSendStrategy(
            @Qualifier("ukr-properties") Properties props) {
        this.props = props;
    }

    @Override
    public Email sendWithStrategyEmail(Email email, MailBox mailBox, MultipartFile file) {
        try {
            Session session = createSession(mailBox);
            MimeMessage msg = createMessage(session, email, mailBox);
            if (file != null && !file.isEmpty()) {
                MimeBodyPart messageBodyPart = new MimeBodyPart();

                Multipart multipart = new MimeMultipart();

                MimeBodyPart textBodyPart = new MimeBodyPart();
                textBodyPart.setText(email.getBody(), "utf-8");
                multipart.addBodyPart(textBodyPart);

                messageBodyPart.setDataHandler(new DataHandler(new ByteArrayDataSource(file.getInputStream(), file.getContentType())));
                messageBodyPart.setFileName(file.getOriginalFilename());
                multipart.addBodyPart(messageBodyPart);

                msg.setContent(multipart);
            }
            Transport.send(msg);
            email.setSentAt(LocalDateTime.now());
            return email;

        } catch (MessagingException | IOException e) {
            throw new SendException("Failed to send email: " + e.getMessage());
        }
    }

    @Override
    public void checkIsPasswordCorrect(MailBox mailBox) {
        try (Transport transport = createSession(mailBox).getTransport(PROTOCOL_SMTP)) {
            transport.connect(mailBox.getEmailConfiguration().getHost(),
                    mailBox.getEmailAddress(),
                    mailBox.getAccessSmtp());
        } catch (AuthenticationFailedException e) {
            throw new SendException(
                    "Invalid credentials for mailbox: " + mailBox.getEmailAddress());
        } catch (MessagingException e) {
            throw new SendException("Failed to verify credentials: " + e.getMessage());
        }
    }


    private Session createSession(MailBox mailBox) {
        return Session.getInstance(props, new jakarta.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mailBox.getEmailAddress(),
                        mailBox.getAccessSmtp());
            }
        });
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
