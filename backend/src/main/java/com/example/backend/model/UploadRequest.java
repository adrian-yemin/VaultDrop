package com.example.backend.model;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UploadRequest {
    private MultipartFile file;
    private boolean oneTimeUse;
    private String expiresAt;
}
