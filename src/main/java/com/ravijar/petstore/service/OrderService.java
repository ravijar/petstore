package com.ravijar.petstore.service;

import com.ravijar.petstore.repository.OrderRepository;
import com.ravijar.petstore.model.Order;
import com.ravijar.petstore.model.CartItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartService cartService;

    public Mono<Order> placeOrder(String userId) {
        return cartService.getCartForUser(userId).collectList()
                .flatMap(cartItems -> {
                    if (cartItems.isEmpty()) return Mono.error(new RuntimeException("Cart is empty"));

                    Order order = new Order();
                    order.setUserId(userId);
                    order.setPetIds(cartItems.stream().map(CartItem::getPetId).toList());
                    order.setOrderTime(Instant.now());
                    order.setStatus("PLACED");

                    return orderRepository.save(order)
                            .flatMap(savedOrder -> cartService.clearCart(userId).thenReturn(savedOrder));
                });
    }

    public Flux<Order> getOrdersByUserId(String userId) {
        return orderRepository.findByUserId(userId);
    }
}

