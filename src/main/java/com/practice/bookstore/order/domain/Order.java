package com.practice.bookstore.order.domain;

import com.practice.bookstore.global.domain.BaseTimeEntity;
import com.practice.bookstore.user.domain.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "orders")
public class Order extends BaseTimeEntity {

    public enum OrderStatus{
        CREATED,
        PAID,
        CANCELED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String orderNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;

    @Column(nullable = false)
    private Long totalAmount;

    @Column(nullable = false)
    private String shippingAddress;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Builder
    public Order(User user, String orderNumber, OrderStatus orderStatus, Long totalAmount, String shippingAddress) {
        this.user = user;
        this.orderNumber = orderNumber;
        this.orderStatus = orderStatus;
        this.totalAmount = totalAmount;
        this.shippingAddress = shippingAddress;
    }

    public void addOrderItem(OrderItem orderItem){
        this.orderItems.add(orderItem);
        orderItem.setOrder(this);
    }


}
