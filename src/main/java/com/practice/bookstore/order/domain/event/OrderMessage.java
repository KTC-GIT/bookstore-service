package com.practice.bookstore.order.domain.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderMessage implements Serializable {
    private String orderId;
    private String userEmail;
    private Long totalPrice;
}
