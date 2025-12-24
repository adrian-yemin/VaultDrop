package com.example.backend.model.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

@Data
public class AnonymousUploadRequest {
    @NotNull(message = "File is required")
    private MultipartFile file;

    @NotNull(message = "One-time use flag is required")
    private Boolean oneTimeUse;

    @NotNull(message = "Max downloads is required")
    @Min(value = 1, message = "Max downloads must be at least 1")
    private Integer maxDownloads;

    private String expiresAt;
}
