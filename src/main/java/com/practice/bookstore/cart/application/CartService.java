package com.practice.bookstore.cart.application;

import com.practice.bookstore.book.application.BookService;
import com.practice.bookstore.book.domain.Book;
import com.practice.bookstore.book.domain.BookRepository;
import com.practice.bookstore.cart.domain.Cart;
import com.practice.bookstore.cart.domain.CartItem;
import com.practice.bookstore.cart.domain.CartItemRepository;
import com.practice.bookstore.cart.domain.CartRepository;
import com.practice.bookstore.cart.presentation.dto.CartDetailResponse;
import com.practice.bookstore.cart.presentation.dto.CartItemResponse;
import com.practice.bookstore.user.domain.User;
import com.practice.bookstore.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Transactional(readOnly = true)
    public List<CartDetailResponse> getCartList(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException("유저 없음."));

        Cart savedCart = cartRepository.findByUserId(user.getId())
                .orElse(null);
        List<CartDetailResponse> responseList = new ArrayList<>();

        if(savedCart != null){
            List<CartItem> cartItems = cartItemRepository.findByCartId(savedCart.getId());
            responseList= cartItems.stream()
                    .map(CartDetailResponse::from)
                    .toList();
        }

        return responseList;
    }

    @Transactional
    public void addToCart(Long userId,Long bookId,int count){
        // 1. 유저정보 확인(없으면 예외처리)
        User user = userRepository.findById(userId)
                .orElseThrow(()->new IllegalArgumentException("유저정보가 없습니다."));

        // 2. 도서정보 확인(없으면 예외처리)
        Book book = bookRepository.findById(bookId)
                .orElseThrow(()-> new IllegalArgumentException("도서정보가 없습니다."));

        // 3. 카트정보 확인(없으면 생성)
        Cart savedCart = cartRepository.findByUserId(user.getId())
                .orElseGet(()-> {
                    Cart newCart = Cart.builder().user(user).build();
                    return cartRepository.save(newCart);
                });

        // 4. 해당 도서가 이미 장바구니에 있는지 확인.
        Optional<CartItem> cartItem = cartItemRepository.findByCartIdAndBookId(savedCart.getId(),bookId);

        if(cartItem.isPresent()){
            // 4-1. 장바구니에 있으면 수량만 늘려줌.
            CartItem item = cartItem.get();
            item.addCount(count);

        }else{
            // 4-2. 장바구니에 없는 경우 CartItem에 정보를 넣어준다.
            CartItem newItem = CartItem.builder()
                    .cart(savedCart)
                    .book(book)
                    .quantity(count)
                    .build();
            cartItemRepository.save(newItem);
        }
    }
}
