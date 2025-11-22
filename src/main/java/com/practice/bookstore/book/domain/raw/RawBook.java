package com.practice.bookstore.book.domain.raw;

import com.practice.bookstore.global.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@ToString
@Table(name = "book_raw", indexes = @Index(name="idx_item_id", columnList = "itemId", unique = true))
public class RawBook extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    //내부 시스템 ID

    //--알라딘 데이터--
    @Column(unique = true, nullable = false)
    private Long itemId;

    @Column(length = 500, nullable = false)
    private String title;
    @Column(length = 500)
    private String author;
    private String publisher;
    private String pubDate;
    private String isbn;
    private String isbn13;

    @Lob
    private String description;

    private Integer priceStandard;
    private Integer priceSales;
    private String mallType;
    private String cover;
    private String categoryName;
    private boolean adult;

    //시리즈 정보(없을 수도 있으니 null 허용)
    private Long seriesId;
    private String seriesName;
    private String seriesLink;


}
