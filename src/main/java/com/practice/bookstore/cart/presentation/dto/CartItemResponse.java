package com.practice.bookstore.cart.presentation.dto;

import com.practice.bookstore.cart.domain.CartItem;

public record CartItemResponse(
        Long id,
        Long cartId,
        Long bookId,
        Integer quantity
) {
    public static CartItemResponse from(CartItem cartItem) {
        return new CartItemResponse(
                cartItem.getId(),
                cartItem.getCart().getId(),
                cartItem.getBook().getId(),
                cartItem.getQuantity()
        );
    }
}
