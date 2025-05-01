package com.ravijar.petstore.repository;

import com.ravijar.petstore.model.Pet;
import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PetRepository extends FirestoreReactiveRepository<Pet> {
}
