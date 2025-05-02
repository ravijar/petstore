package com.ravijar.petstore.repository;

import com.ravijar.petstore.model.User;
import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends FirestoreReactiveRepository<User> {
}
