package com.practice.bookstore.user.presentation.dto;

public record UserJoinRequest(
        String email,
        String password,
        String username,
        String phoneNumber,
        String address
) {
}
