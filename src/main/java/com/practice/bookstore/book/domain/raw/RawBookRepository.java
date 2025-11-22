package com.practice.bookstore.book.domain.raw;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RawBookRepository extends JpaRepository<RawBook, Long> {
}
