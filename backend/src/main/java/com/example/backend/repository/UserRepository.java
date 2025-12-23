package com.example.backend.repository;

import com.example.backend.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    User findByExternalId(UUID externalId);

    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);
}
