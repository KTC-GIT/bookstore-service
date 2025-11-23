package com.practice.bookstore.global.presentation;

import com.practice.bookstore.book.application.BookService;
import com.practice.bookstore.user.domain.User;
import com.practice.bookstore.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final BookService bookService;
    private final UserRepository userRepository;

    //[데이터 이관 버튼]
    // 호출: POST
    @PostMapping("/books/init")
    public String initBooks(){
        int count = bookService.initBooksFromRaw();
        return count+"권의 책 데이터 이관완료.";
    }

    //[테스트 유저 생성버튼]
    @PostMapping("/users/init")
    public String initUsers(){
        if(userRepository.count() > 0){
            return "이미 유저가 있습니다.";
        }

        User user = User.builder()
                .email("test@example.com")
                .password("1")
                .username("test")
                .phoneNumber("010-1234-5678")
                .address("충청남도 연기군 남면 양화리 608")
                .build();

        userRepository.save(user);
        return "테스트 유저(Id:1) 생성완료";
    }
}
