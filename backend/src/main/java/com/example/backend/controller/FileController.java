package com.example.backend.controller;

import com.example.backend.model.request.AnonymousUploadRequest;
import com.example.backend.model.dto.*;
import com.example.backend.model.entity.File;
import com.example.backend.model.entity.User;
import com.example.backend.model.request.ShareLinkRequest;
import com.example.backend.model.response.ApiResponse;
import com.example.backend.service.FileService;
import com.example.backend.service.LinkService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

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

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("ok");
    }

    @PostMapping("/upload_anonymous")
    public ResponseEntity<ApiResponse<String>> uploadFileAnonymous(
            @Valid @ModelAttribute AnonymousUploadRequest uploadRequest
    ) throws IOException {
        File upload = fileService.storeFile(uploadRequest.getFile());
        String shareLink = linkService.createShareLink(new ShareLinkRequest(
                upload.getExternalId(),
                uploadRequest.getOneTimeUse(),
                uploadRequest.getMaxDownloads(),
                uploadRequest.getExpiresAt()
        ));
        return ResponseEntity.ok(new ApiResponse<>(true, "File uploaded successfully", shareLink));
    }

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<Void>> uploadFileAuth(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User user)
    throws IOException {
        fileService.storeFile(file, user);
        return ResponseEntity.ok(new ApiResponse<>(true, "File uploaded successfully", null));
    }

    @GetMapping("/my/files")
    public ResponseEntity<ApiResponse<List<FileDTO>>> listFiles(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(new ApiResponse<>(true, "", fileService.getFilesByUser(user)));
    }

    @DeleteMapping("/my/files/{fileId}")
    public ResponseEntity<ApiResponse<Void>> deleteFile(@PathVariable UUID fileId) throws IOException {
        return ResponseEntity.ok(fileService.deleteFile(fileId));
    }

    @PostMapping("/share")
    public ResponseEntity<ApiResponse<String>> generateShareLink(@RequestBody ShareLinkRequest shareLinkRequest, @AuthenticationPrincipal User user) {
        String shareLink = linkService.createShareLink(shareLinkRequest, user);
        return ResponseEntity.ok(new ApiResponse<>(true, "Share link created successfully", shareLink));
    }

    @GetMapping("/share/{token}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable UUID token) throws IOException {
        return linkService.downloadFile(token);
    }

    @DeleteMapping("/my/share/delete/{token}")
    public ResponseEntity<ApiResponse<Void>> deleteShareLink(@PathVariable UUID token, @AuthenticationPrincipal User user) {
        linkService.deleteShareLink(token, user);
        return ResponseEntity.ok(new ApiResponse<>(true, "Share link deleted successfully", null));
    }

    @GetMapping("/my/share/links")
    public ResponseEntity<ApiResponse<List<ShareLinkDTO>>> listShareLinks(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(new ApiResponse<>(true, "", linkService.getUserShareLinks(user)));
    }
}
