package com.example.imap.service;


import com.example.imap.web.dto.DetailedReceivedEmail;
import jakarta.mail.Message;

public interface ImapService {
    Message[] getEmails(String account, String folderName) throws Exception;
    DetailedReceivedEmail getSpecificEmail(String account, String folderName, int msgnum)
            throws Exception;
    void moveEmail(String account, String sourceFolder, String destinationFolder, int msgnum)
            throws Exception;
    boolean createFolder(String folderName, String account) throws Exception;

}