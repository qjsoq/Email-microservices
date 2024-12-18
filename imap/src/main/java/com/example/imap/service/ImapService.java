package com.example.imap.service;


import com.example.imap.domain.MailBox;
import com.example.imap.web.dto.DetailedReceivedEmail;
import jakarta.mail.Message;
import java.util.List;

public interface ImapService {
    Message[] getEmails(String account, String folderName, String login) throws Exception;
    DetailedReceivedEmail getSpecificEmail(String account, String folderName, int msgnum, String login)
            throws Exception;
    void moveEmail(String account, String sourceFolder, String destinationFolder, int msgnum, String login)
            throws Exception;
    boolean createFolder(String folderName, String account, String login) throws Exception;
    List<MailBox> getMailBoxes(String login);

}
