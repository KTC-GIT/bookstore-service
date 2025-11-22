package com.practice.bookstore.book.presentation.dto;

import com.practice.bookstore.book.domain.Book;

import java.time.LocalDate;

public record BookDetailResponse(
        Long id,
        String title,
        String author,
        String publisher,
        Long price,
        Long stockQuantity,
        String description,
        String isbn,
        LocalDate publishedAt,
        String thumbnailUrl

) {
    public static BookDetailResponse from(Book book) {
        return new BookDetailResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getPublisher(),
                book.getPrice(),
                book.getStockQuantity(),
                book.getDescription(),
                book.getIsbn(),
                book.getPublishedAt(),
                book.getThumbnailUrl()
        );
    }
}
