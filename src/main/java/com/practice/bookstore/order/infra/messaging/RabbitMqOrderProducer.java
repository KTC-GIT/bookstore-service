package com.practice.bookstore.order.infra.messaging;

import com.practice.bookstore.order.domain.Order;
import com.practice.bookstore.order.domain.OrderEventPublisher;
import com.practice.bookstore.order.domain.event.OrderMessage;
import com.practice.bookstore.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitMqOrderProducer implements OrderEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publish(OrderMessage message) {

        // [NEW] 비동기 메시지 발송 (RabbitMQ)
        // "bookstore.exchange" 라는 우체국에 "mail.key" 라는 주소로 보냄
        rabbitTemplate.convertAndSend("bookstore.exchange","mail.key",message);

        log.info("rabbitMQ 메시지 발송 완료: {}",message.getOrderId());
    }
}
