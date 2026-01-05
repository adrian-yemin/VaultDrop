package com.example.backend.service;

import com.example.backend.model.dto.ShareLinkDTO;
import com.example.backend.model.request.ShareLinkRequest;
import com.example.backend.model.entity.File;
import com.example.backend.model.entity.ShareLink;
import com.example.backend.model.entity.User;
import com.example.backend.repository.FileRepository;
import com.example.backend.repository.ShareLinkRepository;
import com.example.backend.service.storage.LocalStorageService;
import com.example.backend.utils.StringUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
        import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LinkService {
    private final FileRepository fileRepository;
    private final ShareLinkRepository shareLinkRepository;
    private final LocalStorageService localStorageService;

    @Transactional
    public ResponseEntity<byte[]> downloadFile(UUID externalId) throws IOException {
        ShareLink shareLink = getValidShareLink(externalId);
        File file = shareLink.getFile();
        byte[] data = localStorageService.read(file.getExternalId());
        recordDownload(shareLink);
        return buildDownloadResponse(file, data);
    }

    private ShareLink getValidShareLink(UUID externalId) throws IOException {
        ShareLink shareLink = shareLinkRepository.findByExternalId(externalId)
                .orElseThrow(() -> new IOException("Share link not found"));

        if (shareLink.getExpiresAt() != null &&
                Instant.now().isAfter(shareLink.getExpiresAt())) {
            cleanupShareLink(shareLink);
            throw new IOException("Link expired");
        }

        if (shareLink.getMaxDownloads() > 0 &&
                shareLink.getDownloadCount() >= shareLink.getMaxDownloads()) {
            cleanupShareLink(shareLink);
            throw new IOException("Download limit exceeded");
        }

        return shareLink;
    }

    private void recordDownload(ShareLink shareLink) throws IOException {
        shareLink.incrementDownloadCount();

        if (shareLink.isOneTimeUse() || shareLink.getDownloadCount() >= shareLink.getMaxDownloads()) {
            cleanupShareLink(shareLink);
        }
    }

    private void cleanupShareLink(ShareLink shareLink) throws IOException {
        File file = shareLink.getFile();

        shareLinkRepository.delete(shareLink);
        shareLinkRepository.flush();

        if (file.getUser() == null && file.getShareLinks().isEmpty()) {
            fileRepository.delete(file);
            localStorageService.delete(file.getExternalId());
        }
    }

    private ResponseEntity<byte[]> buildDownloadResponse(File file, byte[] data) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(file.getMimeType()));
        headers.setContentLength(data.length);
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename(file.getOriginalFilename())
                        .build()
        );

        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }

    public String createShareLink(ShareLinkRequest shareLinkRequest) {
        ShareLink link = new ShareLink(
                fileRepository.findByExternalId(shareLinkRequest.getFileId()),
                shareLinkRequest.getExpiresAt() != null ? Instant.parse(shareLinkRequest.getExpiresAt()) : null,
                shareLinkRequest.isOneTimeUse(),
                shareLinkRequest.getMaxDownloads()
        );
        shareLinkRepository.save(link);
        return StringUtils.createShareLinkUrl(link.getExternalId());
    }

    public String createShareLink(ShareLinkRequest shareLinkRequest, User user) {
        ShareLink link = new ShareLink(
                fileRepository.findByExternalId(shareLinkRequest.getFileId()),
                shareLinkRequest.getExpiresAt() != null ? Instant.parse(shareLinkRequest.getExpiresAt()) : null,
                shareLinkRequest.isOneTimeUse(),
                shareLinkRequest.getMaxDownloads(),
                user
        );
        shareLinkRepository.save(link);
        System.out.println(link.getUser().getUsername());
        return StringUtils.createShareLinkUrl(link.getExternalId());
    }

    public void deleteShareLink(UUID externalId, User user) {
        ShareLink shareLink = shareLinkRepository.findByExternalId(externalId)
                .orElseThrow(() -> new RuntimeException("Share link not found"));
        if (shareLink.getUser() != null && !shareLink.getUser().equals(user)) {
            throw new RuntimeException("Unauthorized: You do not own this share link");
        }
        shareLinkRepository.delete(shareLink);
    }

    public List<ShareLinkDTO> getUserShareLinks(User user) {
        List<ShareLink> shareLinkEntities = shareLinkRepository.findAllByUser(user);
        return shareLinkEntities.stream().map(ShareLinkDTO::new).toList();
    }
}
