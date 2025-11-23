package com.practice.bookstore.cart.presentation.dto;

import com.practice.bookstore.cart.domain.CartItem;

public record CartDetailResponse(
        Long cartItemId,
        Long bookId,
        String title,
        String thumbnailUrl,
        Integer quantity,
        Long price,
        Long totalPrice
) {
    public static CartDetailResponse from(CartItem cartItem) {
        return new CartDetailResponse(
                cartItem.getId(),
                cartItem.getBook().getId(),
                cartItem.getBook().getTitle(),
                cartItem.getBook().getThumbnailUrl(),
                cartItem.getQuantity(),
                cartItem.getBook().getPrice(),
                cartItem.getBook().getPrice() * cartItem.getQuantity()
        );
    }
}
