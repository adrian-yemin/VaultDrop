package com.example.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.util.UUID;
import java.time.Instant;

@Entity
@Data
public class ShareLinkEntity {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    private FileEntity file;

    private String token;
    private Instant expiresAt;
    private boolean oneTimeUse;
    private boolean used = false;
    private int downloadCount = 0;
}
