package com.library.framework.models.dto.books;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BookResponseRecord(
        Long id,
        String title,
        String author,
        String isbn,
        String readingStatus,
        String format,
        boolean available
) {
}
