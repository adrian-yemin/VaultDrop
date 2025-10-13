package com.example.backend.service;

import com.example.backend.model.File;
import com.example.backend.model.User;
import com.example.backend.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {
    private final FileRepository fileRepository;

    private final Path storageDir = Paths.get("uploads");

    public File storeFile(MultipartFile fileUpload, User user) throws IOException {
        ensureStorageDirExists();
        Path filePath = storeMultipartFile(fileUpload);
        File file = new File(fileUpload, filePath);
        file.setUser(user);
        fileRepository.save(file);
        return file;
    }

    public File storeFile(MultipartFile fileUpload) throws IOException {
        ensureStorageDirExists();
        Path filePath = storeMultipartFile(fileUpload);
        File file = new File(fileUpload, filePath);
        fileRepository.save(file);
        return file;
    }

    private void ensureStorageDirExists() throws IOException {
        if (Files.notExists(storageDir)) {
            Files.createDirectories(storageDir);
        }
    }

    private Path storeMultipartFile(MultipartFile file) throws IOException {
        Path filePath = storageDir.resolve(Objects.requireNonNull(file.getOriginalFilename()));
        Files.copy(file.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        return filePath;
    }

    public String deleteFile(UUID id) {
        File file = fileRepository.findByExternalId(id);
        if (file == null) {
            return "File not found";
        }
        try {
            Files.deleteIfExists(Paths.get(file.getStoragePath()));
            fileRepository.delete(file);
            return "File deleted successfully";
        } catch (IOException e) {
            return "Error deleting file: " + e.getMessage();
        }
    }
}
