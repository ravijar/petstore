package com.ravijar.petstore.repository;

import com.ravijar.petstore.model.Item;
import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends FirestoreReactiveRepository<Item> {
}
