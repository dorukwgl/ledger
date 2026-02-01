package com.doruk.infrastructure.persistence.mapper;

import com.doruk.application.dto.PageResponse;
import org.babyfish.jimmer.Page;

import java.util.function.Function;

public class PageMapper {
    public static <T, U> PageResponse<T>
    toResponse(Page<U> page, Function<U, T> mapper) {
        return PageResponse.<T>builder()
                .totalPageCount(page.getTotalPageCount())
                .totalRowCount(page.getTotalRowCount())
                .data(page.getRows().stream().map(mapper).toList())
                .build();
    }
}
