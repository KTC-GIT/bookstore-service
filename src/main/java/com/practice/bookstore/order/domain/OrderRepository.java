package com.practice.bookstore.order.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("select o from Order o "+
            "join fetch o.orderItems oi "+
            "join fetch oi.book b "+
            "where o.user.id =:userId "+
            "Order by o.createdAt desc")
    List<Order> findByUserId(Long userId);
}
