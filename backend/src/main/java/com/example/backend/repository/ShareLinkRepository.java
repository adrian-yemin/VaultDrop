package com.example.backend.repository;

import com.example.backend.model.ShareLinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShareLinkRepository extends JpaRepository<ShareLinkEntity, UUID> {
    Optional<ShareLinkEntity> findByToken(String token);
}
