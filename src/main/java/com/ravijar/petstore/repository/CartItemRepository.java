package com.ravijar.petstore.repository;

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;
import com.ravijar.petstore.model.CartItem;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface CartItemRepository extends FirestoreReactiveRepository<CartItem> {
    Flux<CartItem> findByUserId(String userId);
    Mono<Void> deleteByUserId(String userId);
}

