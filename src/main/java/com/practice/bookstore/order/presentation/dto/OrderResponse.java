package com.practice.bookstore.order.presentation.dto;

import com.practice.bookstore.order.domain.Order;
import com.practice.bookstore.order.domain.OrderItem;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        String orderNumber,
        Order.OrderStatus status,
        LocalDateTime createdDate,
        Long totalAmount,
        String shippingAddress,
        List<OrderItemInfo> items
) {
    public static OrderResponse from(Order order){
        return new OrderResponse(
                order.getOrderNumber(),
                order.getOrderStatus(),
                order.getCreatedAt(),
                order.getTotalAmount(),
                order.getShippingAddress(),
                order.getOrderItems().stream().map(OrderItemInfo::from).toList()
        );
    }

    public record OrderItemInfo(
            String bookTitle,
            int quantity,
            long unitPrice,
            long totalPrice
    ){
        public static OrderItemInfo from(OrderItem orderItem){
            return new OrderItemInfo(
                    orderItem.getBookTitle(),
                    orderItem.getQuantity(),
                    orderItem.getUnitPrice(),
                    orderItem.getTotalPrice()
            );
        }
    }
}
