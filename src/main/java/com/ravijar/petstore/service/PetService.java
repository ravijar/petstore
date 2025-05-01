package com.ravijar.petstore.service;

import com.ravijar.petstore.model.Pet;
import com.ravijar.petstore.repository.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PetService {
    @Autowired
    private PetRepository petRepository;

    public Flux<Pet> getAllPets() {
        return petRepository.findAll();
    }

    public Mono<Pet> getPetById(String id) {
        return petRepository.findById(id);
    }

    public Mono<Pet> savePet(Pet pet) {
        return petRepository.save(pet);
    }

    public Mono<Void> deletePet(String id) {
        return petRepository.deleteById(id);
    }

    public Mono<Pet> updatePet(String id, Pet updatedPet) {
        return petRepository.findById(id)
                .flatMap(existingPet -> {
                    existingPet.setName(updatedPet.getName());
                    existingPet.setSpecies(updatedPet.getSpecies());
                    existingPet.setDescription(updatedPet.getDescription());
                    existingPet.setImageURL(updatedPet.getImageURL());
                    return petRepository.save(existingPet);
                });
    }
}
