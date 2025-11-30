package com.practice.bookstore.notification.interfaces;

import com.practice.bookstore.notification.application.MailService;
import com.practice.bookstore.order.domain.event.OrderMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitMqMailConsumer {
    private final MailService mailService;

    // 큐 이름(bookstore.queue)을 듣고 있음
    @RabbitListener(queues = "bookstore.queue")
    public void sendMail(OrderMessage message){
        mailService.sendOrderCompleteMail(
                message.getUserEmail(),
                message.getOrderId(),
                message.getTotalPrice()
        );
    }
}
