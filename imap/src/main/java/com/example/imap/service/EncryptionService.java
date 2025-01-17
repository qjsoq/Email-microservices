package com.example.imap.service;

public interface EncryptionService {
    String encrypt(String plainText);
    String decrypt(String encryptedText);
}
