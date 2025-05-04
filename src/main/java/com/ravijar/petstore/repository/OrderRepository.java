package com.ravijar.petstore.repository;

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;
import com.ravijar.petstore.model.Order;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface OrderRepository extends FirestoreReactiveRepository<Order> {
    Flux<Order> findByUserId(String userId);
}

