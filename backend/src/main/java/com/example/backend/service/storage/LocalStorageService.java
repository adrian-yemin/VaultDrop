package com.example.backend.service.storage;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class LocalStorageService implements StorageService {
    private final Path root = Paths.get("storage");

    @PostConstruct
    public void init() throws IOException {
        Files.createDirectories(root);
    }

    @Override
    public Path save(UUID key, InputStream data) throws IOException {
        Path target = root.resolve(key.toString());
        Files.copy(data, target);
        return target;
    }

    @Override
    public byte[] read(UUID key) throws IOException {
        return Files.readAllBytes(root.resolve(key.toString()));
    }

    @Override
    public void delete(UUID key) throws IOException {
        Files.deleteIfExists(root.resolve(key.toString()));
    }
}
