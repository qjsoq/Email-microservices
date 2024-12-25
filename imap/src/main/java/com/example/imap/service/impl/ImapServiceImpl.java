package com.example.imap.service.impl;


import com.example.imap.domain.MailBox;
import com.example.imap.exception.InvalidEmailReaderException;
import com.example.imap.exception.MoveFolderException;
import com.example.imap.exception.PropertiesNotFoundException;
import com.example.imap.exception.ReadException;
import com.example.imap.repository.MailBoxRepository;
import com.example.imap.service.ImapService;
import com.example.imap.web.dto.DetailedReceivedEmail;
import com.example.imap.web.dto.MailBoxDto;
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
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImapServiceImpl implements ImapService {
    private final List<Properties> listOfImapProperties;
    private final MailBoxRepository mailBoxRepository;
    private Properties imapProperties;

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

    @Override
    public Folder getFolderFromStore(Store store, String folderName, int type)
            throws MessagingException {
        Folder folder = store.getFolder(folderName);
        folder.open(type);
        return folder;
    }

    @Override
    public Store getImapStore(String account, String login) throws Exception {
        var mailbox = mailBoxRepository.findByEmailAddressAndUserLogin(account, login).get();
        var imapConfig = mailbox.getEmailConfiguration();
        setProperties(imapConfig.getImapHost());
        Session session = Session.getInstance(imapProperties);
        Store store = session.getStore("imap");
        try {
            store.connect(imapConfig.getImapHost(), account, mailbox.getAccessSmtp());
        } catch (Exception e) {
            throw new ReadException(e.getMessage());
        }
        return store;
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


    @Override
    public Message[] getEmails(String account, String folderName, String login) throws Exception {
        isUserAllowedToReadEmail(login, account);
        Store store = getImapStore(account, login);
        Folder folder = getFolderFromStore(store, folderName, Folder.READ_ONLY);
        int messageCount = folder.getMessageCount();
        if (messageCount == 0) {
            return new Message[0];
        }
        int start = Math.max(1, messageCount - 30);
        Message[] messages = folder.getMessages(start, messageCount);
        folder.fetch(messages, getFetchProfile());
        closeFolder(folder);
        closeStore(store);
        return messages;
    }

    private Message getEmail(String account, String folderName, int msgnum, String login)
            throws Exception {
        Store store = getImapStore(account, login);
        Folder folder = getFolderFromStore(store, folderName, Folder.READ_WRITE);
        var message = folder.getMessage(msgnum);
        message.setFlag(Flags.Flag.SEEN, true);
        return message;
    }

    @Override
    public DetailedReceivedEmail getSpecificEmail(String account, String folderName, int msgnum,
                                                  String login)
            throws Exception {
        isUserAllowedToReadEmail(login, account);
        Message message = getEmail(account, folderName, msgnum, login);
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
                          int msgnum, String login) throws Exception {
        Store store = getImapStore(account, login);
        Folder source = getFolderFromStore(store, sourceFolder, Folder.READ_WRITE);
        Message[] messages = new Message[] {source.getMessage(msgnum)};

        Folder destination = getFolderFromStore(store, destinationFolder, Folder.READ_WRITE);
        try {
            source.copyMessages(messages, destination);
        } catch (Exception e) {
            throw new MoveFolderException(String.format("Error while moving email from %s to %s",
                    sourceFolder, destinationFolder));
        }
        source.close();
        destination.close();
    }


    @Override
    public boolean createFolder(String folderName, String account, String login) throws Exception {
        Store store = getImapStore(account, login);
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

    private String[] getFolderNames(Store store) throws MessagingException {
        return Arrays.stream(store.getDefaultFolder().list("*")).map(Folder::getFullName)
                .toArray(String[]::new);
    }

    @Override
    public List<MailBoxDto> getMailBoxes(String login) {
        List<MailBox> mailBoxes = mailBoxRepository.findByUserLogin(login);

        List<MailBoxDto> mailBoxDtos = mailBoxes.stream()
                .map(mailBox -> {
                    try {
                        MailBoxDto mailBoxDto = new MailBoxDto();
                        mailBoxDto.setEmailAddress(mailBox.getEmailAddress());

                        Store store = getImapStore(mailBox.getEmailAddress(), login);
                        String[] folders = getFolderNames(store);
                        closeStore(store);
                        mailBoxDto.setFolders(folders);

                        return mailBoxDto;
                    } catch (Exception e) {
                        e.printStackTrace();
                        MailBoxDto mailBoxDto = new MailBoxDto();
                        mailBoxDto.setEmailAddress(mailBox.getEmailAddress());
                        mailBoxDto.setFolders(new String[0]);
                        return mailBoxDto;
                    }
                })
                .collect(Collectors.toList());

        return mailBoxDtos;
    }


    private String getTextFromMessage(Message message) throws MessagingException, IOException {
        if (message.getContent() instanceof MimeMultipart mimeMultipart) {
            System.out.println("mime message");
            return getTextFromMimeMultipart(mimeMultipart);
        }
        System.out.println("Plain text");
        return message.getContent().toString();
    }

    private void isUserAllowedToReadEmail(String login, String account) {
        mailBoxRepository.findByEmailAddressAndUserLogin(account, login)
                .orElseThrow(() -> new InvalidEmailReaderException("this is not your email"));
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
