package com.ravijar.petstore.controller;

import com.ravijar.petstore.model.Order;
import com.ravijar.petstore.service.AuthService;
import com.ravijar.petstore.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private AuthService authService;

    @PostMapping("/place")
    public Mono<Order> placeOrder(@RequestHeader("Authorization") String token) {
        return authService.extractUserId(token)
                .flatMap(orderService::placeOrder);
    }

    @GetMapping
    public Flux<Order> getOrders(@RequestHeader("Authorization") String token) {
        return authService.extractUserId(token)
                .flatMapMany(orderService::getOrdersByUserId);
    }
}

