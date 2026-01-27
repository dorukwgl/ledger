package com.doruk.presentation.dto;

import com.doruk.application.enums.SortOrder;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.annotation.Nullable;

@Serdeable
public record PageQueryRequest(
        @Nullable
        @QueryValue
        Integer page,

        @Nullable
        @QueryValue
        Integer size,

        @Nullable
        SortOrder order
        ) {
        @Override
        public Integer page() {
                return page == null ? 0 : page - 1;
        }

        @Override
        public Integer size() {
                return size == null ? 10 : size;
        }

        @Override
        public SortOrder order() {
                return order == null ? SortOrder.DESC : order;
        }
}
