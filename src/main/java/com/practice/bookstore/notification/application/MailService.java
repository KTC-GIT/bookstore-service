package com.practice.bookstore.notification.application;

import com.practice.bookstore.order.domain.event.OrderMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    public void sendOrderCompleteMail(String email, String orderId, Long amount) {
        // 나중에 진짜 SMTP(JavaMailSender) 코드가 들어감
        log.info("========================================");
        log.info("📧 [이메일 발송기] 주문 접수 확인!");
        log.info("주문 번호: {}",orderId);
        log.info("수신자: {}",email);
        log.info("결제 금액: {}",amount);
        log.info(">> 메일 전송 성공 (가상)");
        log.info("========================================");

//        log.info(">>> [MailService] To: {}, Title: 주문({})완료, 주문금액: {}", email, orderId, amount);
    }
}
