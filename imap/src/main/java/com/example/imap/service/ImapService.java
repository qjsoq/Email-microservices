package com.example.imap.service;


import com.example.imap.domain.Email;
import com.example.imap.web.dto.DetailedReceivedEmail;
import com.example.imap.web.dto.EmailDto;
import com.example.imap.web.dto.MailBoxDto;
import com.example.imap.web.dto.ReceivedEmail;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Store;
import java.util.List;

public interface ImapService {
    ReceivedEmail[] getEmails(String account, String folderName, String login, int mailNum) throws Exception;
    Message getEmail(String account, String folderName, int msgnum, String login)
            throws Exception;

    Store getImapStore(String account, String login) throws Exception;

    Folder getFolderFromStore(Store store, String folderName, int type) throws MessagingException;

    DetailedReceivedEmail getSpecificEmail(String account, String folderName, int msgnum,
                                           String login)
            throws Exception;

    void moveEmail(String account, String sourceFolder, String destinationFolder, int msgnum,
                   String login)
            throws Exception;

    boolean createFolder(String folderName, String account, String login) throws Exception;

    List<MailBoxDto> getMailBoxes(String login);
    List<Email> getSavedEmails(String login);

}
