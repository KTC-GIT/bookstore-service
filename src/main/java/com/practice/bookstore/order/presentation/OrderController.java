package com.practice.bookstore.order.presentation;

import com.practice.bookstore.order.application.OrderService;
import com.practice.bookstore.order.domain.OrderItem;
import com.practice.bookstore.order.presentation.dto.OrderCreateFromCartReq;
import com.practice.bookstore.order.presentation.dto.OrderResponse;
import com.practice.bookstore.user.application.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/items")
    @ResponseBody
    public String orderItems(@AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody OrderCreateFromCartReq request){
        String orderNo = orderService.createOrderFromCart(userDetails.getId(),request);
        return "주문이 완료되었습니다. \n"+
                "주문번호 : "+orderNo;
    }

    @GetMapping("/list")
    public String orderList(@AuthenticationPrincipal CustomUserDetails userDetails,
            Model model){
        List<OrderResponse> responses = orderService.getOrderList(userDetails.getId());
        model.addAttribute("orders", responses);
        return "order/list";
    }

}
