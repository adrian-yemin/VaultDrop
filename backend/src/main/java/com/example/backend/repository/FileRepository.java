package com.example.backend.repository;

import com.example.backend.model.entity.File;
import com.example.backend.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
    File findByExternalId(UUID externalId);

    List<File> findAllByUser(User user);
}
