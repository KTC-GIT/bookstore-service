package com.practice.bookstore.notification.domain;

import com.practice.bookstore.global.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "mail_history")
public class MailHistory extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String recipient;

    @Column(nullable = true)
    private String orderId;

    @Column(nullable = false)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MailStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MailType mailType;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    @Column(nullable = true)
    private LocalDateTime sentAt;

    @Builder
    public MailHistory(String recipient, String subject, String content, MailType mailType) {
        this.recipient = recipient;
        this.subject = subject;
        this.content = content;
        this.status = MailStatus.PENDING;   // 기본값은 대기
        this.mailType = mailType;
    }

    // 성공 처리 메서드
    public void markAsSent(){
        this.status = MailStatus.SENT;
        this.sentAt = LocalDateTime.now();
    }

    // 실패 처리 메서드
    public void markAsFailed(String error){
        this.status = MailStatus.FAILED;
        this.errorMessage = error;
    }

    // 주문 번호 추가 메서드
    public void markOrderId(String orderId){
        this.orderId = orderId;
    }



    public enum MailStatus{
        PENDING,    // 발송 대기 (RabbitMQ에서 막 꺼낸 상태)
        SENT,   // 발송 성공
        FAILED  // 발송 실패 (나중에 배치가 재발송 예정)
    }

    public enum MailType{
        ORDER_CONFIRMATION,
        MARKETING,
        FIND_ACCOUNT
    }
}


