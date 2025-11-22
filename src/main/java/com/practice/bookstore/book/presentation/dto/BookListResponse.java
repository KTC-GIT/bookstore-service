package com.practice.bookstore.book.presentation.dto;


import com.practice.bookstore.book.domain.Book;

// Java 16+ Record 사용 (Lombok 없이 깔끔)
public record BookListResponse (
    Long id,
    String title,
    String author,
    String publisher,
    Long price,
    String thumbnailUrl
){
    //이미지가 없으면 보여줄 기본이미지( 회색배경)
    private static final String DEFAULT_THUMBNAIL = "https://via.placeholder.com/200x300?text=No+Image";

    public static BookListResponse from(Book book) {
        //Null safe 처리 : 이미지가 없거나 빈 문자열이면 기본 이미지 사용
        String safeImageUrl = (book.getThumbnailUrl() == null || book.getThumbnailUrl().isBlank())
                ? DEFAULT_THUMBNAIL
                :book.getThumbnailUrl();

        return new BookListResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getPublisher(),
                book.getPrice(),
                safeImageUrl
        );
    }
}
