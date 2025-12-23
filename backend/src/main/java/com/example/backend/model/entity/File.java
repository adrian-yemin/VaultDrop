package com.example.backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.time.Instant;

@Entity
@Table(name = "files")
@Data
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID externalId = UUID.randomUUID();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "file", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShareLink> shareLinks;

    private String originalFilename;
    private String storagePath;
    private String mimeType;
    private long size;
    private Instant uploadedAt = Instant.now();

    public File() {}

    public File(MultipartFile file, Path filePath) {
        this.originalFilename = file.getOriginalFilename();
        this.storagePath = filePath.toString();
        this.mimeType = file.getContentType();
        this.size = file.getSize();
    }

    public File(MultipartFile file, Path path, User user) {
        this.originalFilename = file.getOriginalFilename();
        this.storagePath = path.toString();
        this.mimeType = file.getContentType();
        this.size = file.getSize();
        this.user = user;
    }
}
