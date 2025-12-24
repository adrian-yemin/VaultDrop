package com.example.backend.controller;

import com.example.backend.model.dto.FileDTO;
import com.example.backend.model.dto.ShareLinkDTO;
import com.example.backend.model.entity.File;
import com.example.backend.model.response.ApiResponse;
import com.example.backend.service.FileService;
import com.example.backend.service.LinkService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = FileController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.REGEX,
                pattern = "com\\.example\\.backend\\.config\\.security\\..*"
        )
)
@AutoConfigureMockMvc(addFilters = false)
public class FileControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FileService fileService;

    @MockitoBean
    private LinkService linkService;

    @Test
    void uploadAnonymous_success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "hello".getBytes()
        );

        File stored = new File();
        stored.setExternalId(UUID.randomUUID());

        when(fileService.storeFile(any())).thenReturn(stored);
        when(linkService.createShareLink(any())).thenReturn("link");

        mockMvc.perform(multipart("/api/upload_anonymous")
                        .file(file)
                        .param("oneTimeUse", "true")
                        .param("maxDownloads", "5")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("link"));
    }

    @Test
    void uploadAnonymous_missingFile_returns400() throws Exception {
        mockMvc.perform(multipart("/api/upload_anonymous"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser()
    void uploadAuthenticated_success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "hello".getBytes()
        );

        mockMvc.perform(multipart("/api/upload")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(fileService).storeFile(any(MultipartFile.class), any());
    }

    @Test
    @WithMockUser
    void listFiles_success() throws Exception {
        FileDTO stub = new FileDTO();
        stub.setId(UUID.randomUUID());
        stub.setOriginalFilename("test.txt");
        stub.setSize(12345L);
        stub.setUploadedAt(Instant.now());

        when(fileService.getFilesByUser(any()))
                .thenReturn(List.of(stub));

        mockMvc.perform(get("/api/my/files"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].originalFilename").value("test.txt"))
                .andExpect(jsonPath("$.data[0].size").value(12345));
    }

    @Test
    @WithMockUser
    void deleteFile_success() throws Exception {
        UUID id = UUID.randomUUID();

        when(fileService.deleteFile(eq(id)))
                .thenReturn(new ApiResponse<>(true, "", null));

        mockMvc.perform(delete("/api/my/files/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser
    void deleteFile_invalidUUID_returns400() throws Exception {
        mockMvc.perform(delete("/api/my/files/not-a-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void generateShareLink_success() throws Exception {
        when(linkService.createShareLink(any(), any()))
                .thenReturn("share-link");

        mockMvc.perform(post("/api/share")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                  "fileId": "00000000-0000-0000-0000-000000000000"
                }
            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("share-link"));
    }

    @Test
    void downloadFile_success() throws Exception {
        byte[] data = "hello".getBytes();

        when(linkService.downloadFile(any()))
                .thenReturn(ResponseEntity.ok(data));

        mockMvc.perform(get("/api/share/{token}", UUID.randomUUID()))
                .andExpect(status().isOk())
                .andExpect(content().bytes(data));
    }

    @Test
    @WithMockUser
    void listShareLinks_success() throws Exception {
        ShareLinkDTO stub = new ShareLinkDTO();
        stub.setToken(UUID.randomUUID());
        stub.setFileId(UUID.randomUUID());
        stub.setOneTimeUse(false);
        stub.setMaxDownloads(5);
        stub.setExpiresAt(null);

        when(linkService.getUserShareLinks(any()))
                .thenReturn(List.of(stub));

        mockMvc.perform(get("/api/my/share/links"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].token").value(stub.getToken().toString()));
    }

    @Test
    @WithMockUser
    void deleteShareLink_success() throws Exception {
        UUID id = UUID.randomUUID();

        doNothing().when(linkService).deleteShareLink(eq(id), any());

        mockMvc.perform(delete("/api/my/share/delete/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser
    void deleteShareLink_invalidUUID_returns400() throws Exception {
        mockMvc.perform(delete("/api/my/share/delete/not-a-uuid"))
                .andExpect(status().isBadRequest());
    }
}
