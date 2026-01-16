package com.example.backend.service.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class S3StorageService implements StorageService {

    private final S3Client s3Client;

    @Value("${S3_BUCKET_NAME}")
    private String bucketName;

    public S3StorageService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public Path save(UUID key, InputStream data) throws IOException {
        byte[] bytes = data.readAllBytes();
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key.toString())
                        .build(),
                RequestBody.fromBytes(bytes)
        );
        return Path.of(key.toString());
    }

    @Override
    public byte[] read(UUID key) throws IOException {
        try (InputStream s3Stream = s3Client.getObject(
                GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key.toString())
                        .build()
        )) {
            return s3Stream.readAllBytes();
        }
    }

    @Override
    public void delete(UUID key) throws IOException {
        s3Client.deleteObject(
                DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key.toString())
                        .build()
        );
    }
}
