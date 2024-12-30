package com.example.email.service.impl;

import com.example.email.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
//import software.amazon.awssdk.core.sync.RequestBody;
//import software.amazon.awssdk.services.s3.S3Client;
//import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
//import software.amazon.awssdk.services.s3.model.GetObjectRequest;
//import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class AwsS3StorageService {//implements StorageService {

//    private final S3Client client;
//
//    @Override
//    public void uploadFile(byte[] file, String objectKey, String bucket) {
//        var request = PutObjectRequest.builder()
//                .bucket(bucket)
//                .key(objectKey)
//                .build();
//        client.putObject(request, RequestBody.fromBytes(file));
//    }
//
//    @Override
//    public Resource findByKey(String objectKey, String bucket) {
//        var request = GetObjectRequest.builder()
//                .bucket(bucket)
//                .key(objectKey)
//                .build();
//        return new InputStreamResource(client.getObject(request));
//    }
//
//    @Override
//    public void deleteByKey(String objectKey, String bucket) {
//        var request = DeleteObjectRequest.builder()
//                .bucket(bucket)
//                .key(objectKey)
//                .build();
//        client.deleteObject(request);
//    }
}
