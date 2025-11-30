package com.practice.bookstore.global.presentation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RabbitMqTestController {
    // [TODO] 테스트용 컨트롤러로 추후 삭제
    private final RabbitTemplate rabbitTemplate;

    // 1. 테스트 큐 생성 (이게 있어야 메시지를 담음)
    @Bean
    public Queue testQueue() {
        return new Queue("test-Queue",false);
    }

    // 2. Producer: 브라우저에서 /mq/test 치면 메시지 발송
    @GetMapping("/mq/test")
    public String sendMessage(){
        String message = "Hello RabbitMQ! " + System.currentTimeMillis();
        rabbitTemplate.convertAndSend("test-Queue",message);
        log.info("📤 [보냄] : {}", message);
        return "Message sent: " + message;
    }

    // 3. Consumer: 큐에 메시지가 들어오면 즉시 낚아채서 로그 찍음
    @RabbitListener(queues = "test-Queue")
    public void receiveMessage(String message){
        log.info("📥 [받음] : {}", message);
    }
}
