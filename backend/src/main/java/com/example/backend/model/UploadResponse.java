package com.example.backend.model;

import lombok.Data;

@Data
public class UploadResponse {
    private String message;

    public UploadResponse(String message) {
        this.message = message;
    }
}
