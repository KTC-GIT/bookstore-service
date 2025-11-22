package com.practice.bookstore.global.presentation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class GlobalApiController {

    @GetMapping("/health")
    public String healthCheck(){
        return "OK";
    }
}
