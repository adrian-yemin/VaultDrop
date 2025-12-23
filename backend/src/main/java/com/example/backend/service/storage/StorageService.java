package com.example.backend.service.storage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.UUID;

public interface StorageService {
    Path save(UUID key, InputStream data) throws IOException;
    byte[] read(UUID key) throws IOException;
    void delete(UUID key) throws IOException;
}
