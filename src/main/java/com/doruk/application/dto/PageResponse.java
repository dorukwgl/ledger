package com.doruk.application.dto;

import io.micronaut.serde.annotation.Serdeable;
import lombok.Builder;

import java.util.List;

@Serdeable
@Builder
public record PageResponse<T>(
        List<T> data,
        long totalRowCount,
        long totalPageCount
) {
}
