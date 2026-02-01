package com.doruk.presentation.dto;

import com.doruk.application.dto.PageQuery;

public class PageQueryMapper {
    public static PageQuery toQuery(final PageQueryRequest pageQueryRequest) {
        return new PageQuery(
                pageQueryRequest.page(),
                pageQueryRequest.size(),
                pageQueryRequest.order()
        );
    }
}
