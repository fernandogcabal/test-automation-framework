package com.library.framework.models.dto.common;

import java.util.List;

public record PageResponseRecord<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {
}
