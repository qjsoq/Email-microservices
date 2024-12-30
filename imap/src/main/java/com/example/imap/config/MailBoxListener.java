package com.example.imap.config;


import com.example.imap.domain.MailBox;
import com.example.imap.service.EncryptionService;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class MailBoxListener {

    private static EncryptionService encryptionService;

    @Autowired
    public void setEncryptionService(EncryptionService encryptionService) {
        MailBoxListener.encryptionService = encryptionService;
    }

    @PrePersist
    @PreUpdate
    public void encryptPasswords(MailBox mailBox) {
        System.out.println("Encrypting passwords");
        if (mailBox.getAccessSmtp() != null) {
            mailBox.setAccessSmtp(encryptionService.encrypt(mailBox.getAccessSmtp()));
        }
        if (mailBox.getRefreshToken() != null) {
            mailBox.setRefreshToken(encryptionService.encrypt(mailBox.getRefreshToken()));
        }
    }

    @PostLoad
    public void decryptPasswords(MailBox mailBox) {
        if (mailBox.getAccessSmtp() != null) {
            mailBox.setAccessSmtp(encryptionService.decrypt(mailBox.getAccessSmtp()));
        }
        if (mailBox.getRefreshToken() != null) {
            mailBox.setRefreshToken(encryptionService.decrypt(mailBox.getRefreshToken()));
        }
    }
}
