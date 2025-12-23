package com.example.backend.model.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class AnonymousUploadRequest {
    private MultipartFile file;
    private boolean oneTimeUse;
    private int maxDownloads;
    private String expiresAt;
}
