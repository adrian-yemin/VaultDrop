package com.example.backend.model.dto;

import com.example.backend.model.entity.File;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class FileDTO {
    private UUID id;
    private String originalFilename;
    private long size;
    private Instant uploadedAt;

    public FileDTO(File entity) {
        this.id = entity.getExternalId();
        this.originalFilename = entity.getOriginalFilename();
        this.size = entity.getSize();
        this.uploadedAt = entity.getUploadedAt();
    }
}
