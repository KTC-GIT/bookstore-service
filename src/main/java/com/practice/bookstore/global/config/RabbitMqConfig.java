package com.practice.bookstore.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RabbitMqConfig {
    // 스프링이 "자바 직렬화" 대신 "JSON"을 사용
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // 큐 생성 (durable=true: 서버 재시작해도 유지됨)
    @Bean
    public Queue bookstoreQueue() {
        return new Queue("bookstore.queue",true);
    }

    // 익스체인지 생성
    @Bean
    public DirectExchange bookstoreExchange() {
        return new DirectExchange("bookstore.exchange");
    }

    // 바인딩 (큐와 익스체인지를 'mail.key'로 연결
    @Bean
    public Binding binding(Queue bookstoreQueue, DirectExchange bookstoreExchange) {
        return BindingBuilder.bind(bookstoreQueue)
                .to(bookstoreExchange)
                .with("mail.key");
    }
}
