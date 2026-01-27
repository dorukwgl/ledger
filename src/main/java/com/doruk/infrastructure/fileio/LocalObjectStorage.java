package com.doruk.infrastructure.fileio;

import com.doruk.application.dto.StoredObject;
import com.doruk.application.enums.FileType;
import com.doruk.application.enums.ObjectVisibility;
import com.doruk.application.exception.FileUploadException;
import com.doruk.application.interfaces.ObjectStorage;
import com.doruk.application.interfaces.UploadSource;
import com.doruk.infrastructure.config.AppConfig;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.time.Duration;
import java.util.Locale;
import java.util.UUID;

@Singleton
@Bean(typed = ObjectStorage.class)
@Requires(env = "object-storage-local")
@RequiredArgsConstructor
public class LocalObjectStorage implements ObjectStorage {
    private final AppConfig config;

    @Override
    public StoredObject store(
            UploadSource source,
            FileType type,
            ObjectVisibility visibility,
            long maxSize
    ) {
        validate(source, type, maxSize);

        String original = source.originalFilename();
        String ext = extractExtension(original);

        String uuid = UUID.randomUUID().toString().replace("-", "");
        String shard1 = uuid.substring(0, 2);
        String shard2 = uuid.substring(2, 4);

        String prefix = visibility == ObjectVisibility.PUBLIC ? config.publicPathPrefix() : config.privatePathPrefix();
        String objectKey = Path.of(prefix, shard1, shard2, uuid + ext).toString();
        Path target = Path.of(config.localStorageDir(), objectKey);

        try {
            Files.createDirectories(target.getParent());
            try (InputStream in = source.openStream()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }

        return new StoredObject(
                objectKey,
                original,
                source.size(),
                visibility,
                source.contentType().toLowerCase(Locale.ROOT)
        );
    }

    @Override
    public InputStream open(String objectKey) {
        try {
            Path path = Path.of(config.localStorageDir(), objectKey);
            return Files.newInputStream(path, StandardOpenOption.READ);
        } catch (IOException e) {
            throw new RuntimeException("Failed to open local object: ", e);
        }
    }

    @Override
    public void put(String objectKey, InputStream data, long size, String mimeType) {
        try {
            Path target = Paths.get(config.localStorageDir(), objectKey);
            Files.copy(data, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write local object: " + objectKey, e);
        }
    }

    @Override
    public void delete(String objectKey) {
        try {
            Path target = Paths.get(config.localStorageDir(), objectKey);
            Files.deleteIfExists(target);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete local object: " + objectKey, e);
        }
    }

    @Override
    public String resolveUrl(StoredObject storedObject) {
        if (storedObject.visibility() != ObjectVisibility.PUBLIC) {
            throw new IllegalStateException("Private objects cannot be resolved directly");
        }

        return config.appUrl() + config.resourceApiPath() + storedObject.objectKey();
    }

    @Override
    public String signUrl(StoredObject storedObject, Duration ttl) {
        if (storedObject.visibility() != ObjectVisibility.PRIVATE) {
            return resolveUrl(storedObject);
        }
        return config.appUrl() + config.resourceApiPath() + storedObject.objectKey();
    }

    private void validate(UploadSource source, FileType type, long maxSize) {

        if (source.size() > maxSize) {
            throw new FileUploadException("File too large");
        }

        String mime = source.contentType().toLowerCase(Locale.ROOT);
        if (!type.isAllowed(mime)) {
            throw new FileUploadException("Unsupported content type: " + mime);
        }
    }

    private String extractExtension(String filename) {
        int idx = filename.lastIndexOf('.');
        return idx > 0 ? filename.substring(idx) : "";
    }
}
