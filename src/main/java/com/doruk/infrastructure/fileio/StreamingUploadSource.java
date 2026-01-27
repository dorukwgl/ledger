package com.doruk.infrastructure.fileio;

import com.doruk.application.interfaces.UploadSource;
import io.micronaut.http.multipart.StreamingFileUpload;

import java.io.BufferedInputStream;

public final class StreamingUploadSource implements UploadSource {

    private final StreamingFileUpload upload;

    public StreamingUploadSource(StreamingFileUpload upload) {
        this.upload = upload;
    }

    @Override
    public BufferedInputStream openStream() {
        return new BufferedInputStream(upload.asInputStream(), 8192);
    }

    @Override
    public String originalFilename() {
        return upload.getFilename();
    }

    @Override
    public long size() {
        return upload.getSize();
    }

    @Override
    public String contentType() {
        return upload.getContentType().map(Object::toString).orElse("");
    }
}

