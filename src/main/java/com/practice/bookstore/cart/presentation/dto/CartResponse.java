package com.practice.bookstore.cart.presentation.dto;

import com.practice.bookstore.cart.domain.Cart;

public record CartResponse(
        Long id,
        Long userId
) {
    public static CartResponse from(Cart cart) {
        return new CartResponse(
                cart.getId(),
                cart.getUser().getId()
        );
    }
}
