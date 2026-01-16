package com.example.backend.service;

import com.example.backend.model.dto.FileDTO;
import com.example.backend.model.entity.File;
import com.example.backend.model.entity.User;
import com.example.backend.model.response.ApiResponse;
import com.example.backend.repository.FileRepository;
import com.example.backend.service.storage.S3StorageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FileServiceTest {
    @Mock
    private FileRepository fileRepository;

    @Mock
    private S3StorageService s3StorageService;

    @InjectMocks
    private FileService fileService;

    @Test
    void storeFile_anonymous_success() throws Exception {
        MultipartFile multipartFile =
                new MockMultipartFile("file", "test.txt", "text/plain", "hello".getBytes());

        Path fakePath = Paths.get("uploads/fake");

        when(s3StorageService.save(any(UUID.class), any()))
                .thenReturn(fakePath);

        File result = fileService.storeFile(multipartFile);

        assertNotNull(result);
        assertNotNull(result.getExternalId());

        verify(s3StorageService).save(any(UUID.class), any());
        verify(fileRepository).save(any(File.class));
    }

    @Test
    void storeFile_authenticated_success() throws Exception {
        MultipartFile multipartFile =
                new MockMultipartFile("file", "test.txt", "text/plain", "hello".getBytes());

        User user = new User();
        user.setExternalId(UUID.randomUUID());

        Path fakePath = Paths.get("uploads/fake");

        when(s3StorageService.save(any(UUID.class), any()))
                .thenReturn(fakePath);

        fileService.storeFile(multipartFile, user);

        verify(s3StorageService).save(any(UUID.class), any());
        verify(fileRepository).save(argThat(file ->
                file.getUser() != null && file.getUser().equals(user)
        ));
    }

    @Test
    void deleteFile_fileExists_success() throws Exception {
        UUID id = UUID.randomUUID();

        File file = mock(File.class);
        when(file.getExternalId()).thenReturn(id);

        when(fileRepository.findByExternalId(id))
                .thenReturn(file);

        ApiResponse<Void> response = fileService.deleteFile(id);

        assertTrue(response.isSuccess());

        verify(fileRepository).delete(file);
        verify(s3StorageService).delete(id);
    }

    @Test
    void deleteFile_fileNotFound_returnsFailure() throws Exception {
        UUID id = UUID.randomUUID();

        when(fileRepository.findByExternalId(id))
                .thenReturn(null);

        ApiResponse<Void> response = fileService.deleteFile(id);

        assertFalse(response.isSuccess());
        assertEquals("File not found", response.getMessage());

        verify(fileRepository, never()).delete(any());
        verify(s3StorageService, never()).delete(any());
    }

    @Test
    void getFilesByUser_success() {
        User user = new User();
        user.setExternalId(UUID.randomUUID());

        File file1 = mock(File.class);
        File file2 = mock(File.class);

        when(fileRepository.findAllByUser(user))
                .thenReturn(List.of(file1, file2));

        List<FileDTO> result = fileService.getFilesByUser(user);

        assertEquals(2, result.size());
        verify(fileRepository).findAllByUser(user);
    }
}
