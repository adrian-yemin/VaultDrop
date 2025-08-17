package com.example.backend.controller;

import com.example.backend.model.UploadRequest;
import com.example.backend.model.UploadResponse;
import com.example.backend.service.FileService;
import com.example.backend.service.LinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class FileController {
    private final FileService fileService;
    private final LinkService linkService;

    @Autowired
    public FileController(LinkService linkService, FileService fileService) {
        this.fileService = fileService;
        this.linkService = linkService;
    }

    @PostMapping("/upload")
    public UploadResponse uploadDrop(@ModelAttribute UploadRequest uploadRequest) {
        try {
            String url = fileService.storeFile(uploadRequest);
            return new UploadResponse(url);
        }
        catch (Exception e) {
            return new UploadResponse(null, e.toString());
        }
    }

    @GetMapping("/download/{token}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String token) {
        try {
            return linkService.downloadDrop(token);
        } catch (IOException e) {
            throw new RuntimeException("Error retrieving file: " + e.getMessage());
        }
    }
}
