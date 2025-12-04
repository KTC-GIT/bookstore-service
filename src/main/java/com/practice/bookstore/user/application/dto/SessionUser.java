package com.practice.bookstore.user.application.dto;

import com.practice.bookstore.user.domain.User;

import java.io.Serializable;

// Redis Json 직렬화를 써도, DTO에는 관례적으로 Serializable을 붙여두기도 함. (선택)
public record SessionUser(
        Long id,
        String email,
        String name
) implements Serializable {

    public static SessionUser from(User user){
        return new SessionUser(
                user.getId(),
                user.getEmail(),
                user.getUsername()
        );
    }
}
