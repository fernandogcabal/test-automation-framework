package com.library.framework.models.dto.books;

public record UpdateBookRequestRecord(
        String isbn,
        String title
) {
}
