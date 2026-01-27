package com.doruk.application.enums;

import java.util.Set;

public enum FileType {
    IMAGE(Set.of("image/jpeg", "image/jpg", "image/png", "image/webp", "image/gif")),
    VIDEO(Set.of("video/mp4", "video/webm", "video/quicktime")),
    DOCUMENT(Set.of("application/pdf")),
    ANY(Set.of());

    private final Set<String> mimeTypes;

    FileType(Set<String> mimeTypes) {
        this.mimeTypes = mimeTypes;
    }

    public boolean isAllowed(String mimeType) {
        return this == ANY || mimeTypes.contains(mimeType.toLowerCase());
    }
}