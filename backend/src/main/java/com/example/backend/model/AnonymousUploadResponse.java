package com.example.backend.model;

import lombok.Data;

@Data
public class AnonymousUploadResponse {
    private String linkUrl;
    private Exception error;

    public AnonymousUploadResponse(String linkUrl) {
        this.linkUrl = linkUrl;
        this.error = null;
    }

    public AnonymousUploadResponse(Exception error) {
        this.linkUrl = null;
        this.error = error;
    }
}
