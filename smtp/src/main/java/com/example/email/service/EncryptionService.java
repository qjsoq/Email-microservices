package com.example.email.service;

public interface EncryptionService {
    String encrypt(String plainText);
    String decrypt(String encryptedText);
}
