package com.practice.bookstore.book.application;

import com.practice.bookstore.book.domain.Book;
import com.practice.bookstore.book.domain.BookRepository;
import com.practice.bookstore.book.domain.raw.RawBook;
import com.practice.bookstore.book.domain.raw.RawBookRepository;
import com.practice.bookstore.book.presentation.dto.BookDetailResponse;
import com.practice.bookstore.book.presentation.dto.BookListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final RawBookRepository rawBookRepository;

    //[관리자용] book_raw -> books로 데이터 이관.
    @Transactional
    public int initBooksFromRaw() {
        List<RawBook> rawBooks = rawBookRepository.findAll();

        List<Book> books = rawBooks.stream()
                .map(raw ->Book.builder()
                        .title(raw.getTitle())
                        .author(raw.getAuthor())
                        .publisher(raw.getPublisher())
                        .isbn(raw.getIsbn13())
                        .price(Long.valueOf(raw.getPriceSales()))
                        .stockQuantity(100L)
                        .description(raw.getDescription())
                        .thumbnailUrl(raw.getCover())
                        .publishedAt(parseDateSafely(raw.getPubDate()))
                        .itemId(raw.getItemId())
                        .build())
                .toList();

        bookRepository.saveAll(books);
        return books.size();
    }

    //리스트 조회
    @Transactional(readOnly = true)
    public List<BookListResponse> getBooks(){
        List<Book> books = bookRepository.findAll();

        return books.stream()
                .map(BookListResponse::from)
                .toList();
    }

    // 단건 조회
    @Transactional(readOnly = true) // 조회성능 최적화
    public BookDetailResponse getBookById(Long id){
        Book book = bookRepository.findById(id).orElseThrow(()->
                new IllegalArgumentException("책을 찾을 수 없습니다. id="+id));

        return BookDetailResponse.from(book);
    }


    private LocalDate parseDateSafely(String dateStr){
        if(dateStr == null || dateStr.isEmpty()){
            return null;
        }
        try{
            return LocalDate.parse(dateStr);
        }catch(Exception e){
            return null;
        }
    }
}
