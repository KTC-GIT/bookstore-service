package com.practice.bookstore.book.presentation;

import com.practice.bookstore.book.application.BookService;
import com.practice.bookstore.book.domain.Book;
import com.practice.bookstore.book.domain.BookRepository;
import com.practice.bookstore.book.presentation.dto.BookDetailResponse;
import com.practice.bookstore.book.presentation.dto.BookListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BookController {
    private final BookRepository bookRepository;
    private final BookService bookService;

    @GetMapping("/")
    public String bookList(Model model){
        List<BookListResponse> response = bookService.getBooks();

        model.addAttribute("books", response);
        return "book/list";
    }

    @GetMapping("/books/{id}")
    public String bookDetail(Model model,@PathVariable("id") Long id){
        BookDetailResponse response = bookService.getBookById(id);

        model.addAttribute("book", response);

        return "book/detail";
    }

}
