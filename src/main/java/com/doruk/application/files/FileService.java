package com.doruk.application.files;

import com.doruk.application.dto.UploadedFile;
import com.doruk.application.enums.FileType;
import com.doruk.application.enums.ObjectVisibility;
import com.doruk.application.interfaces.ObjectStorage;
import com.doruk.infrastructure.config.AppConfig;
import com.doruk.infrastructure.fileio.StreamingUploadSource;
import com.doruk.infrastructure.persistence.files.FileRepository;
import io.micronaut.http.multipart.StreamingFileUpload;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor
public class FileService {
    private final ObjectStorage storage;
    private final AppConfig config;
    private final FileRepository fileRepo;

    public UploadedFile imageUploadPublic(StreamingFileUpload upload) {
        var obj = storage.store(new StreamingUploadSource(upload), FileType.IMAGE, ObjectVisibility.PUBLIC, config.imageMaxSize());
        var id = fileRepo.save(obj);
        return new UploadedFile(id, obj);
    }
}
