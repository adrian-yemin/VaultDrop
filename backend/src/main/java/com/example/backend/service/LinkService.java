package com.example.backend.service;

import com.example.backend.model.FileEntity;
import com.example.backend.model.ShareLinkEntity;
import com.example.backend.repository.FileRepository;
import com.example.backend.repository.ShareLinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class LinkService {
    private final ShareLinkRepository shareLinkRepository;
    private final FileRepository fileRepository;

    public ResponseEntity<byte[]> downloadDrop(String token) throws IOException {
        ShareLinkEntity shareLink = shareLinkRepository.findByToken(token)
                .orElseThrow(() -> new IOException("Share link not found"));
        FileEntity file = shareLink.getFile();
        if (shareLink.isOneTimeUse()) {
            shareLinkRepository.delete(shareLink);
            fileRepository.delete(file);
        }
        shareLink.setDownloadCount(shareLink.getDownloadCount() + 1);
        shareLink.setUsed(true);
        byte[] data = Files.readAllBytes(Paths.get(file.getStoragePath()));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(file.getMimeType()));
        headers.setContentLength(data.length);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename(file.getOriginalFilename())
                .build());
        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }

}
