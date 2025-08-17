package com.example.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

import java.nio.file.Path;
import java.util.UUID;
import java.time.Instant;

@Entity
@Data
public class FileEntity {
    @Id
    @GeneratedValue
    private UUID id;

    private String originalFilename;
    private String storagePath;
    private String mimeType;
    private long size;
    private Instant uploadedAt;

    public FileEntity() {

    }

    public FileEntity(UploadRequest vaultDropRequest, Path filePath) {
        this.originalFilename = vaultDropRequest.getFile().getOriginalFilename();
        this.storagePath = filePath.toString();
        this.mimeType = vaultDropRequest.getFile().getContentType();
        this.size = vaultDropRequest.getFile().getSize();
        this.uploadedAt = Instant.now();

    }

}
