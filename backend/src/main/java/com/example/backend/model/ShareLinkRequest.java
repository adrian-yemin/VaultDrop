package com.example.backend.model;

import lombok.Data;

import java.util.UUID;

@Data
public class ShareLinkRequest {
    private UUID fileId;
    private boolean oneTimeUse;
    private int maxDownloads;
    private String expiresAt;

    public ShareLinkRequest(UUID id, boolean oneTimeUse, int maxDownloads, String expiresAt) {
        this.fileId = id;
        this.oneTimeUse = oneTimeUse;
        this.maxDownloads = maxDownloads;
        this.expiresAt = expiresAt;
    }
}
