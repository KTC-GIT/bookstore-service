package com.practice.bookstore.notification.application;

import com.practice.bookstore.notification.domain.MailHistory;
import com.practice.bookstore.notification.infra.MailHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {
    private final MailHistoryRepository mailHistoryRepository;

    @Transactional
    public void sendOrderCompleteMail(String email, String orderId, Long amount) {

        String subject = String.format("주문 완료 안내 (%s", orderId);
        String content = String.format("고객님의 주문이 완료되었습니다. 결제금액: %d",amount);

        // 나중에 진짜 SMTP(JavaMailSender) 코드가 들어감
        log.info("========================================");
        log.info("📧 [이메일 발송기] 주문 접수 확인!");
        log.info("주문 번호: {}",orderId);
        log.info("수신자: {}",email);
        log.info("결제 금액: {}",amount);


        // [INSERT] DB에 메일 히스토리 남기기
        MailHistory history = MailHistory.builder()
                .recipient(email)
                .subject(subject)
                .content(content)
                .mailType(MailHistory.MailType.ORDER_CONFIRMATION)
                .build();
        mailHistoryRepository.save(history);

        history.markOrderId(orderId);    // 주문 메일인 경우에만 넣기 위해 분리함.

        try {
            // [발송] 실제 메일 발송 시도 (SMTP 등)
            // mailSender.send(..);
            log.info(">>> [SMTP 전송 성공] To: {}",email);

            // [성공] 상태 업데이트 (Dirty Checking)
            history.markAsSent();


        }catch (Exception e){
            // [실패] 에러가 나도 DB는 롤백하지 않고 '실패' 상태로 남김!
            log.error(">>> [SMTP 전송 실패] 에러: {}",e.getMessage());
            history.markAsFailed(e.getMessage());

            // 주의: 여기서 throw e를 하면 트랜잭션이 롤백되어 history 자체가 사라짐.
            // "기록을 남기는 것"이 목표이므로 예외를 삼킴.
        }


        log.info(">> 메일 전송 성공 (가상)");
        log.info("========================================");

//        log.info(">>> [MailService] To: {}, Title: 주문({})완료, 주문금액: {}", email, orderId, amount);
    }
}
