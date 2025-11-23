package com.practice.bookstore.user.presentation;

import com.practice.bookstore.user.application.UserService;
import com.practice.bookstore.user.presentation.dto.UserJoinRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/login")
    public String login(){
        return "user/login";
    }

    @GetMapping("/join")
    public String joinPage(){
        return "user/join";
    }

    @PostMapping("/join")
    public String joinProcess(UserJoinRequest request){
        Long id = userService.addUser(request);
        return "redirect:/login";
    }
}
