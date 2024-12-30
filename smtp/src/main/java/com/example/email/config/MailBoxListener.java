package com.example.email.config;

import com.example.email.domain.MailBox;
import com.example.email.service.EncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

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
