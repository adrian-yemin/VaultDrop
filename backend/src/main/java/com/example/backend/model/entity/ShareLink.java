package com.example.backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
import java.time.Instant;

@Entity
@Table(name = "sharelinks")
@Data
@NoArgsConstructor
public class ShareLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID externalId = UUID.randomUUID();

    @ManyToOne
    @JoinColumn(name = "file_id", nullable = false)
    private File file;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Instant expiresAt;
    private boolean oneTimeUse;
    private int downloadCount = 0;
    private int maxDownloads;
    private final Instant createdAt = Instant.now();

    public ShareLink(File file, Instant expiresAt, boolean oneTimeUse, int maxDownloads) {
        this.file = file;
        this.expiresAt = expiresAt;
        this.oneTimeUse = oneTimeUse;
        this.downloadCount = 0;
        this.maxDownloads = maxDownloads;
    }

    public ShareLink(File file, Instant expiresAt, boolean oneTimeUse, int maxDownloads, User user) {
        this.file = file;
        this.expiresAt = expiresAt;
        this.oneTimeUse = oneTimeUse;
        this.downloadCount = 0;
        this.maxDownloads = maxDownloads;
        this.user = user;
    }

    public void incrementDownloadCount() {
        this.downloadCount++;
    }
}
