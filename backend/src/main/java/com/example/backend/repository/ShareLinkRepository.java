package com.example.backend.repository;

import com.example.backend.model.entity.ShareLink;
import com.example.backend.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShareLinkRepository extends JpaRepository<ShareLink, Long> {
    Optional<ShareLink> findByExternalId(UUID externalId);

    List<ShareLink> findAllByUser(User user);
}
