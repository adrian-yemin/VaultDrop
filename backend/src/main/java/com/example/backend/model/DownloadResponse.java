package com.example.backend.model;

import lombok.Data;

@Data
public class DownloadResponse {
    private byte[] fileData;
    private String originalFilename;
    private String mimeType;
}
