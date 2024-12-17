package com.example.imap.service.impl;


import com.example.imap.repository.MailBoxRepository;
import com.example.imap.service.ImapService;
import com.example.imap.web.dto.DetailedReceivedEmail;
import jakarta.mail.Address;
import jakarta.mail.BodyPart;
import jakarta.mail.FetchProfile;
import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.UIDFolder;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.search.ComparisonTerm;
import jakarta.mail.search.ReceivedDateTerm;
import jakarta.mail.search.SearchTerm;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class ImapServiceImpl implements ImapService {
    private final List<Properties> listOfImapProperties;
    private final MailBoxRepository mailBoxRepository;
    private Properties imapProperties;

    public static Folder getFolderFromStore(Store store, String folderName, int type) throws
            MessagingException {
        Folder folder = store.getFolder(folderName);
        folder.open(type);
        return folder;
    }

    public static SearchTerm getMessagesSearchTerm() {
        Date yesterdayDate = new Date(new Date().getTime() - (1000L * 60 * 60 * 24 * 7));
        return new ReceivedDateTerm(ComparisonTerm.GT, yesterdayDate);
    }

    private static FetchProfile getFetchProfile() {
        FetchProfile fetchProfile = new FetchProfile();
        fetchProfile.add(UIDFolder.FetchProfileItem.ENVELOPE);
        fetchProfile.add(UIDFolder.FetchProfileItem.CONTENT_INFO);
        fetchProfile.add("X-mailer");
        return fetchProfile;
    }


    private static void closeFolder(Folder folder) {
        if (folder != null && folder.isOpen()) {
            try {
                folder.close(true);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }

    private static void closeStore(Store store) {
        if (store != null && store.isConnected()) {
            try {
                store.close();
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }

    private Store getImapStore(String account) throws Exception {
        var mailbox = mailBoxRepository.findByEmailAddress(account).orElseThrow(
                () -> new RuntimeException("Mailbox not found for account: " + account));
        var imapConfig = mailbox.getEmailConfiguration();
        setProperties(imapConfig.getImapHost());
        Session session = Session.getInstance(imapProperties);
        Store store = session.getStore("imap");
        store.connect(imapConfig.getImapHost(), account, mailbox.getAccessSmtp());
        return store;
    }

    private void setProperties(String domainName) {
        this.imapProperties = listOfImapProperties.stream()
                .filter(properties -> {
                    String host = properties.getProperty("mail.imap.host");
                    return host != null && host.contains(domainName);
                })
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No matching Properties bean found for domain: " + domainName));
    }


    @Override
    public Message[] getEmails(String account, String folderName) throws Exception {

        Store store = getImapStore(account);
        Folder folder = getFolderFromStore(store, folderName, Folder.READ_ONLY);
        int messageCount = folder.getMessageCount();
        if (messageCount == 0) {
            return new Message[0];
        }
        int start = Math.max(1, messageCount - 14);
        Message[] messages = folder.getMessages(start, messageCount);
        folder.fetch(messages, getFetchProfile());
        closeFolder(folder);
        closeStore(store);
        return messages;
    }

    private Message getEmail(String account, String folderName, int msgnum) throws Exception {
        Store store = getImapStore(account);
        Folder folder = getFolderFromStore(store, folderName, Folder.READ_WRITE);
        var message = folder.getMessage(msgnum);
        message.setFlag(Flags.Flag.SEEN, true);
        return message;
    }

    @Override
    public DetailedReceivedEmail getSpecificEmail(String account, String folderName, int msgnum)
            throws Exception {
        Message message = getEmail(account, folderName, msgnum);
        Address[] fromAddresses = message.getFrom();
        String senderEmail = null;
        if (fromAddresses != null && fromAddresses.length > 0) {
            InternetAddress address = (InternetAddress) fromAddresses[0];
            senderEmail = address.getAddress();
        }

        Address[] recipientAddresses = message.getAllRecipients();
        String receiverEmail = null;
        if (recipientAddresses != null && recipientAddresses.length > 0) {
            InternetAddress address = (InternetAddress) recipientAddresses[0];
            receiverEmail = address.getAddress();
        }

        String subject = message.getSubject();

        String body = getTextFromMessage(message);
        return DetailedReceivedEmail.builder()
                .body(body)
                .receiverEmail(receiverEmail)
                .senderEmail(senderEmail)
                .subject(subject)
                .receivedDate(message.getReceivedDate())
                .build();
    }

    @Override
    public void moveEmail(String account, String sourceFolder, String destinationFolder,
                          int msgnum) throws Exception {
        Store store = getImapStore(account);
        Folder source = getFolderFromStore(store, sourceFolder, Folder.READ_WRITE);
        Message[] messages = new Message[] {source.getMessage(msgnum)};

        Folder destination = getFolderFromStore(store, destinationFolder, Folder.READ_WRITE);
        source.copyMessages(messages, destination);
        source.close();
        destination.close();
    }

    @Override
    public boolean createFolder(String folderName, String account) throws Exception {
        Store store = getImapStore(account);
        Folder newFolder = store.getFolder(folderName);
        if (!newFolder.exists()) {
            if (newFolder.create(Folder.HOLDS_MESSAGES)) {
                newFolder.setSubscribed(true);
                return true;
            }
        }
        closeFolder(newFolder);
        closeStore(store);
        return false;
    }

    private String getTextFromMessage(Message message) throws MessagingException, IOException {
        if (message.getContent() instanceof MimeMultipart mimeMultipart) {
            System.out.println("mime message");
            return getTextFromMimeMultipart(mimeMultipart);
        }
        System.out.println("Plain text");
        return message.getContent().toString();
    }

    public String getTextFromMimeMultipart(
            MimeMultipart mimeMultipart) throws MessagingException, IOException {
        String result = "";
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {

                result = result + "\n" + bodyPart.getContent();
                break;

            } else if (bodyPart.isMimeType("text/html")) {
                String html = (String) bodyPart.getContent();
                result = result + "\n" + html; //org.jsoup.Jsoup.parse(html).text();
            } else if (bodyPart.getContent() instanceof MimeMultipart) {
                result = result + getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent());

            }
        }
        return result;
    }
}