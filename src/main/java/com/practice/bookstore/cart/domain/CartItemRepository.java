package com.practice.bookstore.cart.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @Query("select ci from CartItem ci join fetch ci.book where ci.cart.id =:cartId")
    List<CartItem> findByCartId(Long cartId);

    Optional<CartItem> findByCartIdAndBookId(Long cartId, Long bookId);
}
