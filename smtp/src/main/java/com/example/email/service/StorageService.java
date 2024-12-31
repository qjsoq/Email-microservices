package com.example.email.service;


import org.springframework.core.io.Resource;

public interface StorageService {
    void uploadFile(byte[] file, String objectKey, String bucket);

    Resource findByKey(String objectKey, String bucket);

    void deleteByKey(String objectKey, String bucket);
    boolean canConnectToBucket(String bucketName);
}