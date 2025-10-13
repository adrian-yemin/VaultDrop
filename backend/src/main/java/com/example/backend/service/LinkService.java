package com.example.backend.service;

import com.example.backend.model.File;
import com.example.backend.model.ShareLink;
import com.example.backend.model.ShareLinkRequest;
import com.example.backend.repository.FileRepository;
import com.example.backend.repository.ShareLinkRepository;
import com.example.backend.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LinkService {
    private final FileRepository fileRepository;
    private final ShareLinkRepository shareLinkRepository;

    public ResponseEntity<byte[]> downloadFile(UUID token) throws IOException {
        ShareLink shareLink = shareLinkRepository.findByToken(token)
                .orElseThrow(() -> new IOException("Share link not found"));
        File file = shareLink.getFile();
        if (shareLink.isOneTimeUse()) {
            shareLinkRepository.delete(shareLink);
            fileRepository.delete(file);
        }
        shareLink.setDownloadCount(shareLink.getDownloadCount() + 1);
        byte[] data = Files.readAllBytes(Paths.get(file.getStoragePath()));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(file.getMimeType()));
        headers.setContentLength(data.length);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename(file.getOriginalFilename())
                .build());
        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }

    public String createShareLink(ShareLinkRequest shareLinkRequest) {
        ShareLink link = new ShareLink();
        link.setFile(fileRepository.findByExternalId(shareLinkRequest.getFileId()));
        link.setOneTimeUse(shareLinkRequest.isOneTimeUse());
        link.setMaxDownloads(shareLinkRequest.getMaxDownloads());
        String expiresAtStr = shareLinkRequest.getExpiresAt();
        if (expiresAtStr != null) {
            Instant expiresAt = Instant.parse(expiresAtStr);
            link.setExpiresAt(expiresAt);
        }
        shareLinkRepository.save(link);
        return StringUtils.createShareLinkUrl(link.getToken());
    }

    public void deleteShareLink(UUID token) {
        ShareLink shareLink = shareLinkRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Share link not found"));
        shareLinkRepository.delete(shareLink);
    }
}
