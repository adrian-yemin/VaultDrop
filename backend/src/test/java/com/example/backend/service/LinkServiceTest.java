package com.example.backend.service;

import com.example.backend.model.dto.ShareLinkDTO;
import com.example.backend.model.entity.File;
import com.example.backend.model.entity.ShareLink;
import com.example.backend.model.entity.User;
import com.example.backend.model.request.ShareLinkRequest;
import com.example.backend.repository.FileRepository;
import com.example.backend.repository.ShareLinkRepository;
import com.example.backend.service.storage.LocalStorageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LinkServiceTest {
    @Mock
    private FileRepository fileRepository;

    @Mock
    private ShareLinkRepository shareLinkRepository;

    @Mock
    private LocalStorageService localStorageService;

    @InjectMocks
    private LinkService linkService;

    @Test
    void downloadFile_success() throws Exception {
        UUID token = UUID.randomUUID();
        UUID fileId = UUID.randomUUID();

        File file = mock(File.class);
        when(file.getExternalId()).thenReturn(fileId);
        when(file.getMimeType()).thenReturn("text/plain");
        when(file.getOriginalFilename()).thenReturn("test.txt");

        ShareLink link = mock(ShareLink.class);
        when(link.getFile()).thenReturn(file);
        when(link.getExpiresAt()).thenReturn(null);
        when(link.getMaxDownloads()).thenReturn(10);
        when(link.getDownloadCount()).thenReturn(0);
        when(link.isOneTimeUse()).thenReturn(false);

        when(shareLinkRepository.findByExternalId(token))
                .thenReturn(Optional.of(link));

        byte[] data = "hello".getBytes();
        when(localStorageService.read(fileId)).thenReturn(data);

        ResponseEntity<byte[]> response = linkService.downloadFile(token);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertArrayEquals(data, response.getBody());
        verify(localStorageService).read(fileId);
    }

    @Test
    void downloadFile_expiredLink_throwsException() {
        UUID token = UUID.randomUUID();
        File file = mock(File.class);

        ShareLink link = mock(ShareLink.class);
        when(link.getExpiresAt()).thenReturn(Instant.now().minusSeconds(60));
        when(link.getFile()).thenReturn(file);

        when(shareLinkRepository.findByExternalId(token))
                .thenReturn(Optional.of(link));

        assertThrows(IOException.class,
                () -> linkService.downloadFile(token));
    }

    @Test
    void downloadFile_maxDownloadsExceeded_throwsException() {
        UUID token = UUID.randomUUID();
        File file = mock(File.class);

        ShareLink link = mock(ShareLink.class);
        when(link.getExpiresAt()).thenReturn(null);
        when(link.getMaxDownloads()).thenReturn(1);
        when(link.getDownloadCount()).thenReturn(1);
        when(link.getFile()).thenReturn(file);

        when(shareLinkRepository.findByExternalId(token))
                .thenReturn(Optional.of(link));

        assertThrows(IOException.class,
                () -> linkService.downloadFile(token));
    }

    @Test
    void downloadFile_oneTimeUse_deletesFileAndLink() throws Exception {
        UUID token = UUID.randomUUID();
        UUID fileId = UUID.randomUUID();

        File file = mock(File.class);
        when(file.getExternalId()).thenReturn(fileId);
        when(file.getUser()).thenReturn(null);
        when(file.getMimeType()).thenReturn("text/plain");
        when(file.getOriginalFilename()).thenReturn("test.txt");

        ShareLink link = mock(ShareLink.class);
        when(link.getFile()).thenReturn(file);
        when(link.getExpiresAt()).thenReturn(null);
        when(link.getMaxDownloads()).thenReturn(1);
        when(link.getDownloadCount()).thenReturn(0);
        when(link.isOneTimeUse()).thenReturn(true);

        when(shareLinkRepository.findByExternalId(token))
                .thenReturn(Optional.of(link));

        when(localStorageService.read(fileId)).thenReturn(new byte[]{1});

        linkService.downloadFile(token);

        verify(shareLinkRepository).delete(link);
        verify(fileRepository).delete(file);
        verify(localStorageService).delete(fileId);
    }

    @Test
    void createShareLink_success() {
        UUID fileId = UUID.randomUUID();

        ShareLinkRequest request = new ShareLinkRequest(
                fileId,
                false,
                3,
                Instant.now().plusSeconds(60).toString()
        );

        File file = new File();
        when(fileRepository.findByExternalId(fileId)).thenReturn(file);

        String result = linkService.createShareLink(request);

        assertNotNull(result);
        verify(shareLinkRepository).save(any());
    }

    @Test
    void deleteShareLink_owner_success() {
        UUID id = UUID.randomUUID();
        User user = new User();

        ShareLink link = mock(ShareLink.class);
        when(link.getUser()).thenReturn(user);

        when(shareLinkRepository.findByExternalId(id))
                .thenReturn(Optional.of(link));

        linkService.deleteShareLink(id, user);

        verify(shareLinkRepository).delete(link);
    }

    @Test
    void deleteShareLink_notOwner_throwsException() {
        UUID id = UUID.randomUUID();

        User owner = new User();
        User attacker = new User();

        ShareLink link = mock(ShareLink.class);
        when(link.getUser()).thenReturn(owner);

        when(shareLinkRepository.findByExternalId(id))
                .thenReturn(Optional.of(link));

        assertThrows(RuntimeException.class,
                () -> linkService.deleteShareLink(id, attacker));
    }

    @Test
    void getUserShareLinks_success() {
        User user = new User();
        File file = new File();

        ShareLink link = mock(ShareLink.class);
        when(link.getFile()).thenReturn(file);
        when(link.getExternalId()).thenReturn(UUID.randomUUID());
        when(shareLinkRepository.findAllByUser(user))
                .thenReturn(List.of(link));

        List<ShareLinkDTO> result = linkService.getUserShareLinks(user);

        assertEquals(1, result.size());
    }
}
