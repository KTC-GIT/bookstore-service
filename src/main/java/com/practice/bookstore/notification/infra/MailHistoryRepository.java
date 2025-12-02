package com.practice.bookstore.notification.infra;

import com.practice.bookstore.notification.domain.MailHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MailHistoryRepository extends JpaRepository<MailHistory, Integer> {
}
