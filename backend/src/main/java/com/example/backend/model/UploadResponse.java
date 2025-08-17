package com.example.backend.model;

import lombok.Data;

@Data
public class UploadResponse {
    private String link;
    private String error;

    public UploadResponse() {}

    public UploadResponse(String link) {
        this.link = link;
    }

    public UploadResponse(String link, String error) {
        this.link = link;
        this.error = error;
    }
}
