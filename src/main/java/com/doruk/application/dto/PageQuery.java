package com.doruk.application.dto;

import com.doruk.application.enums.SortOrder;

public record PageQuery (int page, int size, SortOrder order) {
}
