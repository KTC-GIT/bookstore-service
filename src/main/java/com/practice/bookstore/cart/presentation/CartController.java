package com.practice.bookstore.cart.presentation;

import com.practice.bookstore.cart.application.CartService;
import com.practice.bookstore.cart.presentation.dto.CartDetailResponse;
import com.practice.bookstore.cart.presentation.dto.CartItemResponse;
import com.practice.bookstore.user.application.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/carts")
@RequiredArgsConstructor
@Slf4j
public class CartController {
    private final CartService cartService;

    @GetMapping("/list")
    public String cartList(@AuthenticationPrincipal CustomUserDetails userDetails,
            Model model){

        if(userDetails == null){    //SecurityConfig에서 막긴 할테지만.. 안전장치
            return "redirect:/login";
        }

        List<CartDetailResponse> response = cartService.getCartList(userDetails.getId());
        long totalOrderPrice = response.stream().mapToLong(CartDetailResponse::totalPrice).sum();

        model.addAttribute("cartItems", response);
        model.addAttribute("totalOrderPrice", totalOrderPrice);
        return "cart/list";
    }

    @PostMapping("/items")
    @ResponseBody
    public String addCartItem(@AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody CartItemResponse cartItemResponse){
        cartService.addToCart(userDetails.getId(),cartItemResponse.bookId(),cartItemResponse.quantity());
        return "장바구니 담기 성공";
    }
}
