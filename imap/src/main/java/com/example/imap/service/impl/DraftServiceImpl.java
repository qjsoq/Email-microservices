package com.example.imap.service.impl;

import com.example.imap.domain.Email;
import com.example.imap.exception.InvalidEmailReaderException;
import com.example.imap.exception.PropertiesNotFoundException;
import com.example.imap.repository.MailBoxRepository;
import com.example.imap.service.DraftService;
import com.example.imap.service.ImapService;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DraftServiceImpl implements DraftService {
    private final List<Properties> listOfImapProperties;
    private final MailBoxRepository mailBoxRepository;
    private final ImapService imapService;
    private Properties imapProperties;

    @Override
    public void saveEmailAsDraft(Email email, String login) throws Exception {
        var mailbox =
                mailBoxRepository.findByEmailAddressAndUserLogin(email.getSenderEmail(), login)
                        .orElseThrow(
                                () -> new InvalidEmailReaderException(
                                        "This email does not exist in your mailboxes.")
                        );
        var imapConfig = mailbox.getEmailConfiguration();
        setProperties(imapConfig.getImapHost());
        Session session = Session.getInstance(imapProperties);
        var store = imapService.getImapStore(email.getSenderEmail(), login);

        var folders = store.getDefaultFolder().list("*");
        var draftFolder = Arrays.stream(folders)
                .filter(folder -> {
                    System.out.println(folder.getFullName().toLowerCase());
                    System.out.println(folder.getFullName().toLowerCase().contains("draft"));
                    return folder.getName().toLowerCase().contains("draft");
                })
                .findFirst()
                .orElseThrow(() -> new Exception("Draft folder not found"));

        draftFolder.open(Folder.READ_WRITE);


        var mimeMessage = new MimeMessage(session);
        mimeMessage.setFrom(new InternetAddress(email.getSenderEmail()));
        mimeMessage.setRecipients(Message.RecipientType.TO, email.getRecipientEmail());
        mimeMessage.setSubject(email.getSubject());
        mimeMessage.setContent(email.getBody(), "text/html");

        Message[] messages = {mimeMessage};
        draftFolder.appendMessages(messages);
        draftFolder.close();
    }

    private void setProperties(String domainName) {
        this.imapProperties = listOfImapProperties.stream()
                .filter(properties -> {
                    String host = properties.getProperty("mail.imap.host");
                    return host != null && host.contains(domainName);
                })
                .findFirst()
                .orElseThrow(() -> new PropertiesNotFoundException(
                        "No matching Properties bean found for domain: " + domainName));
    }

}
