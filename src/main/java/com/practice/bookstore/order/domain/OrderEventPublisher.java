package com.practice.bookstore.order.domain;

import com.practice.bookstore.order.domain.event.OrderMessage;

public interface OrderEventPublisher {
    void publish(OrderMessage message);
}
