package com.practice.bookstore.order.application;

import com.practice.bookstore.book.domain.Book;
import com.practice.bookstore.book.domain.BookRepository;
import com.practice.bookstore.cart.domain.Cart;
import com.practice.bookstore.cart.domain.CartItem;
import com.practice.bookstore.cart.domain.CartItemRepository;
import com.practice.bookstore.cart.domain.CartRepository;
import com.practice.bookstore.order.domain.*;
import com.practice.bookstore.order.domain.event.OrderMessage;
import com.practice.bookstore.order.infra.messaging.RabbitMqOrderProducer;
import com.practice.bookstore.order.presentation.dto.OrderCreateFromCartReq;
import com.practice.bookstore.order.presentation.dto.OrderResponse;
import com.practice.bookstore.user.domain.User;
import com.practice.bookstore.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    private final OrderEventPublisher orderEventPublisher;

    @Transactional
    public String createOrderFromCart(Long userId, OrderCreateFromCartReq request){
        // 1. 유저여부 확인
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException("유저정보가 없음."));

        // 2. 유저 카트여부 확인
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(()->new IllegalArgumentException("카트정보가 없음"));

        // 3. 카트 아이템 확인
        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());

        if(cartItems.isEmpty()){
            throw new IllegalArgumentException("장바구니에 물건이 없음.");
        }

        // 4. 주문 상품(OrderItem) 생성 및 재고차감
        List<OrderItem> orderItems = new ArrayList<OrderItem>();
        long totalAmount = 0L;

        for(CartItem cartItem : cartItems){
            Book book = cartItem.getBook();

            // 4-1. 재고 차감(엔티티 비즈니스 로직 호출)
            book.reduceStockQuantity(cartItem.getQuantity());

            // 4-2. 주문 상품 생성(가격,제목 스냅샷)
            OrderItem orderItem = OrderItem.builder()
                    .book(book)
                    .bookTitle(book.getTitle()) // 제목 박제
                    .unitPrice(book.getPrice()) // 가격 박제
                    .quantity(cartItem.getQuantity())
                    .build();

            orderItems.add(orderItem);
            totalAmount += orderItem.getTotalPrice();
        }

        // 5. 주문정보 생성
        Order order = Order.builder()
                .user(user)
                .orderNumber(generateOrderNumber())
                .orderStatus(Order.OrderStatus.PAID)
                .shippingAddress(request.shippingAddress())
                .totalAmount(totalAmount)
                .build();

        // 6. 연관관계 편의 메서드 대신 수동 세팅(혹은 빌더에서 리스트 받게 수정)
        // JPA CascadeType.ALL 덕분에 order만 저장해도 orderItem들이 같이 저장됨.
        for(OrderItem item : orderItems){
            order.addOrderItem(item);
        }
        orderRepository.save(order);

        // 7. 장바구니 비우기
        cartItemRepository.deleteAll(cartItems);

        // 8. rabbitMQ 비동기 메시지 발송
        OrderMessage message = new OrderMessage(order.getOrderNumber(),user.getEmail(), order.getTotalAmount());
        orderEventPublisher.publish(message);

        return order.getOrderNumber();
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrderList(Long userId){
        // 1. 이용자 확인(없으면 에러)
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException("유저없음."));

        // 2. 주문 확인
        List<Order> orders = orderRepository.findByUserId(userId);
        List<OrderResponse> responses = new ArrayList<>();

        if(!orders.isEmpty()){
            responses = orders.stream()
                    .map(OrderResponse::from)
                    .toList();
        }

        return responses;
    }




    private String generateOrderNumber(){
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + "-" + (int)(Math.random()*1000);
    }
}
