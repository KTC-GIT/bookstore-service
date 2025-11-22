package com.practice.bookstore.book.domain;

import com.practice.bookstore.global.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "books")
public class Book extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private String publisher;
    private String isbn;

    @Column(nullable = false)
    private Long price;

    @Column(nullable = false)
    private Long stockQuantity;

    private String description;
    private String thumbnailUrl;
    private LocalDate publishedAt;

    private Long itemId;

    @Builder
    public Book(String title, String author, String publisher, String isbn, Long price, Long stockQuantity, String description, String thumbnailUrl, LocalDate publishedAt, Long itemId) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.isbn = isbn;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
        this.publishedAt = publishedAt;
        this.itemId = itemId;
    }
}
