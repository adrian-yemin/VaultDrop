package com.example.backend.service;

import com.example.backend.model.response.ApiResponse;
import com.example.backend.model.dto.FileDTO;
import com.example.backend.model.entity.File;
import com.example.backend.model.entity.User;
import com.example.backend.repository.FileRepository;
import com.example.backend.service.storage.LocalStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {
    private final FileRepository fileRepository;
    private final LocalStorageService localStorageService;

    public File storeFile(MultipartFile fileUpload) throws IOException {
        UUID uuid = UUID.randomUUID();
        Path filePath = localStorageService.save(uuid, fileUpload.getInputStream());
        File file = new File(fileUpload, filePath);
        file.setExternalId(uuid);
        fileRepository.save(file);
        return file;
    }

    public void storeFile(MultipartFile fileUpload, User user) throws IOException {
        UUID uuid = UUID.randomUUID();
        Path filePath = localStorageService.save(uuid, fileUpload.getInputStream());
        File file = new File(fileUpload, filePath, user);
        file.setExternalId(uuid);
        fileRepository.save(file);
        System.out.println(file.getUser().getUsername());
    }

    public ApiResponse<Void> deleteFile(UUID id) throws IOException {
        File file = fileRepository.findByExternalId(id);
        if (file == null) {
            return new ApiResponse<>(false, "File not found", null);
        }
        fileRepository.delete(file);
        localStorageService.delete(file.getExternalId());
        return new ApiResponse<>(true, "File deleted successfully", null);
    }

    public List<FileDTO> getFilesByUser(User user) {
        List<File> fileEntities = fileRepository.findAllByUser(user);
        return fileEntities.stream().map(FileDTO::new).toList();
    }
}
