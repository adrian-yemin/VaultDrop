package com.example.backend.repository;

import com.example.backend.model.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
    File findByExternalId(UUID token);
}
