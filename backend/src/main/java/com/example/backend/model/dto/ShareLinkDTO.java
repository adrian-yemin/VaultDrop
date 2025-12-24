package com.example.backend.model.dto;

import com.example.backend.model.entity.ShareLink;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
public class ShareLinkDTO {
    private UUID token;
    private UUID fileId;
    private String fileName;
    private boolean oneTimeUse;
    private int maxDownloads;
    private int downloadsUsed;
    private Instant expiresAt;
    private boolean expired;

    public ShareLinkDTO(ShareLink entity) {
        this.token = entity.getExternalId();
        this.fileId = entity.getFile().getExternalId();
        this.fileName = entity.getFile().getOriginalFilename();
        this.oneTimeUse = entity.isOneTimeUse();
        this.maxDownloads = entity.getMaxDownloads();
        this.downloadsUsed = entity.getDownloadCount();
        this.expiresAt = entity.getExpiresAt();
        this.expired = entity.getExpiresAt() != null && Instant.now().isAfter(entity.getExpiresAt());
    }
}
