package com.example.backend.controller;

import com.example.backend.model.*;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.FileService;
import com.example.backend.service.LinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class FileController {
    private final FileService fileService;
    private final LinkService linkService;

    private final UserRepository userRepository;

    private static final Logger log = LoggerFactory.getLogger(FileController.class);

    @Autowired
    public FileController(LinkService linkService, FileService fileService, UserRepository userRepository) {
        this.fileService = fileService;
        this.linkService = linkService;
        this.userRepository = userRepository;
    }

    @PostMapping("/upload_anonymous")
    public AnonymousUploadResponse uploadFileAnonymous(@ModelAttribute AnonymousUploadRequest uploadRequest) {
        try {
            File upload = fileService.storeFile(uploadRequest.getFile());
            String shareLink = linkService.createShareLink(new ShareLinkRequest(
                    upload.getExternalId(),
                    uploadRequest.isOneTimeUse(),
                    uploadRequest.getMaxDownloads(),
                    uploadRequest.getExpiresAt()
            ));
            return new AnonymousUploadResponse(shareLink);
        }
        catch (Exception e) {
            return new AnonymousUploadResponse(e);
        }
    }

    @PostMapping("/upload/auth")
    public UploadResponse uploadFileAuth(@RequestParam("file") MultipartFile file) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        log.info(">>> Current Authentication = {}", auth);

        try {
            // Extract username from authentication
            String username = auth.getName();

            // Load User entity from your repository
            User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

            // Pass the user to fileService
            fileService.storeFile(file, user);
            return new UploadResponse("File uploaded successfully");
        } catch (IOException e) {
            log.error("Error uploading file", e);
            return new UploadResponse("Error uploading file: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error", e);
            return new UploadResponse("Unexpected error: " + e.getMessage());
        }
    }

    @GetMapping("/my/files")
    public String listFiles() {
        return "List of files";
    }

    @DeleteMapping("/my/files/{fileId}")
    public String deleteFile(@PathVariable UUID fileId) {
        return fileService.deleteFile(fileId);
    }

    @PostMapping("/share")
    public String generateShareLink(@RequestBody ShareLinkRequest shareLinkRequest) {
        return linkService.createShareLink(shareLinkRequest);
    }

    @GetMapping("/share/{token}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable UUID token) {
        try {
            return linkService.downloadFile(token);
        } catch (IOException e) {
            throw new RuntimeException("Error retrieving file: " + e.getMessage());
        }
    }

    @DeleteMapping("/my/share/delete/{token}")
    public String deleteShareLink(@PathVariable UUID token) {
        try {
            linkService.deleteShareLink(token);
            return "Share link deleted successfully";
        } catch (RuntimeException e) {
            return "Error deleting share link: " + e.getMessage();
        }
    }

    @GetMapping("/my/share/links")
    public String listShareLinks() {
        return "List of share links";
    }
}
