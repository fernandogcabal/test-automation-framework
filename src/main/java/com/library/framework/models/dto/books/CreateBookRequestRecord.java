package com.library.framework.models.dto.books;

public record CreateBookRequestRecord(
        String title,
        String author,
        String isbn,
        String readingStatus,
        String format,
        boolean available,
        Long userId
) {
}
