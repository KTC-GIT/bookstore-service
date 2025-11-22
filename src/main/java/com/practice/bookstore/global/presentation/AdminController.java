package com.practice.bookstore.global.presentation;

import com.practice.bookstore.book.application.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final BookService bookService;

    //[데이터 이관 버튼]
    // 호출: POST
    @PostMapping("/books/init")
    public String initBooks(){
        int count = bookService.initBooksFromRaw();
        return count+"권의 책 데이터 이관완료.";
    }
}
