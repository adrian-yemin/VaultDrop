package com.example.backend.service;

import com.example.backend.model.FileEntity;
import com.example.backend.model.ShareLinkEntity;
import com.example.backend.model.UploadRequest;
import com.example.backend.repository.FileRepository;
import com.example.backend.repository.ShareLinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {
    private final FileRepository fileRepository;
    private final ShareLinkRepository shareLinkRepository;

    private final Path storageDir = Paths.get("uploads");

    public String storeFile(UploadRequest uploadRequest) throws IOException {
        ensureStorageDirExists();

        UUID token = UUID.randomUUID();
        Path filePath = storeMultipartFile(uploadRequest.getFile(), token);

        FileEntity file = new FileEntity(uploadRequest, filePath);
        fileRepository.save(file);

        ShareLinkEntity link = createShareLinkEntity(file, uploadRequest, token);
        shareLinkRepository.save(link);

        return "http://localhost:8080/api/download/" + token;
    }

    private void ensureStorageDirExists() throws IOException {
        if (Files.notExists(storageDir)) {
            Files.createDirectories(storageDir);
        }
    }

    private Path storeMultipartFile(MultipartFile file, UUID token) throws IOException {
        String fileName = token + "." + Objects.requireNonNull(file.getContentType()).split("/")[1];
        Path filePath = storageDir.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);
        return filePath;
    }

    private ShareLinkEntity createShareLinkEntity(FileEntity fileEntity, UploadRequest uploadRequest, UUID token) {
        ShareLinkEntity link = new ShareLinkEntity();
        link.setFile(fileEntity);
        link.setToken(token.toString());
        link.setOneTimeUse(uploadRequest.isOneTimeUse());

        String expiresAtStr = uploadRequest.getExpiresAt();
        if (expiresAtStr != null) {
            Instant expiresAt = Instant.parse(expiresAtStr);
            link.setExpiresAt(expiresAt);
        }

        return link;
    }

}
