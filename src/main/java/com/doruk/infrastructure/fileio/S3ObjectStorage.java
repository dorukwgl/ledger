package com.doruk.infrastructure.fileio;

import com.doruk.application.dto.StoredObject;
import com.doruk.application.enums.FileType;
import com.doruk.application.enums.ObjectVisibility;
import com.doruk.application.exception.FileUploadException;
import com.doruk.application.interfaces.ObjectStorage;
import com.doruk.application.interfaces.UploadSource;
import com.doruk.infrastructure.config.AppConfig;
import com.doruk.infrastructure.config.S3Config;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Locale;
import java.util.UUID;

@Singleton
@Bean(typed = ObjectStorage.class)
@Requires(env = "object-storage-s3")
@RequiredArgsConstructor
public class S3ObjectStorage implements ObjectStorage {
    private final S3Client s3;
    private final S3Presigner preSigner;
    private final S3Config config;
    private final AppConfig appConfig;

    @Override
    public StoredObject store(
            UploadSource source,
            FileType type,
            ObjectVisibility visibility,
            long maxSize
    ) {
        validate(source, type, maxSize);

        String objectKey = generateObjectKey(
                visibility,
                source.originalFilename()
        );

        try (InputStream in = source.openStream()) {

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(config.bucket())
                    .key(objectKey)
                    .contentType(source.contentType())
                    .contentLength(source.size())
                    .build();

            s3.putObject(request, RequestBody.fromInputStream(in, source.size()));

        } catch (IOException e) {
            throw new FileUploadException("Failed to upload to object storage", e);
        }

        return new StoredObject(
                objectKey,
                source.originalFilename(),
                source.size(),
                visibility,
                source.contentType().toLowerCase(Locale.ROOT)
        );
    }

    public InputStream open(String objectKey) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(config.bucket())
                .key(objectKey)
                .build();

//        ResponseInputStream<GetObjectResponse> response =
        return s3.getObject(request);

    }

    @Override
    public void put(String objectKey, InputStream data, long size, String mimeType) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(config.bucket())
                .key(objectKey)
                .contentType(mimeType)
                .contentLength(size)
                .build();

        s3.putObject(request, RequestBody.fromInputStream(data, size));
    }

    @Override
    public void delete(String objectKey) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(config.bucket())
                .key(objectKey)
                .build();

        s3.deleteObject(request);
    }

    @Override
    public String resolveUrl(StoredObject storedObject) {
        if (storedObject.visibility() != ObjectVisibility.PUBLIC) {
            throw new IllegalStateException("Cannot resolve public URL for private object");
        }

        return config.publicBaseUrl() + "/" + storedObject.objectKey();
    }

    @Override
    public String signUrl(StoredObject storedObject, Duration ttl) {
        GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(config.bucket())
                .key(storedObject.objectKey())
                .build();

        PresignedGetObjectRequest presigned =
                preSigner.presignGetObject(
                        GetObjectPresignRequest.builder()
                                .getObjectRequest(getRequest)
                                .signatureDuration(ttl)
                                .build()
                );

        return presigned.url().toString();
    }

    // ---------- helpers ----------

    private void validate(UploadSource source, FileType type, long maxSize) {
        if (source.size() > maxSize) {
            throw new FileUploadException("File too large");
        }
        if (!type.isAllowed(source.contentType())) {
            throw new FileUploadException("Invalid MIME type");
        }
    }

    private String generateObjectKey(ObjectVisibility visibility, String filename) {
        String ext = filename.contains(".")
                ? filename.substring(filename.lastIndexOf("."))
                : "";

        String uuid = UUID.randomUUID().toString();
        String hash = uuid.replace("-", "").substring(0, 4);

        return visibility == ObjectVisibility.PUBLIC ?
                appConfig.publicPathPrefix() : appConfig.privatePathPrefix()
                + "/" + hash.substring(0, 2)
                + "/" + hash.substring(2)
                + "/" + uuid + ext;
    }
}

