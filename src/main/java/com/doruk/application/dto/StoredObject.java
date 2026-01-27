package com.doruk.application.dto;

import com.doruk.application.enums.ObjectVisibility;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Builder;

@Serdeable
@Builder
public record StoredObject(
        String objectKey,
        String originalName,
        long size,
        ObjectVisibility visibility,
        String mimeType
) {
}
