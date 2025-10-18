package com.example.backend.model;

import lombok.Data;

@Data
public class UploadResponse {
    private String message;
    private String link;
    private String error;

    public UploadResponse(String message) {
        this.message = message;
    }

    public UploadResponse() {}

    public UploadResponse(String link, String error) {
        this.link = link;
        this.error = error;
    }
}
