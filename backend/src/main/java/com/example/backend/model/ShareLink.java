package com.example.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;
import java.time.Instant;

@Entity
@Data
public class ShareLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID token = UUID.randomUUID();

    @ManyToOne
    @JoinColumn(name = "file_id")
    private File file;

    private Instant expiresAt;
    private boolean oneTimeUse;
    private int downloadCount = 0;
    private int maxDownloads;
    private Instant createdAt = Instant.now();

    public ShareLink() {}

    public ShareLink(File file, Instant expiresAt, boolean oneTimeUse, int downloadCount, int maxDownloads) {
        this.file = file;
        this.expiresAt = expiresAt;
        this.oneTimeUse = oneTimeUse;
        this.downloadCount = downloadCount;
        this.maxDownloads = maxDownloads;
        this.createdAt = Instant.now();
    }
}
