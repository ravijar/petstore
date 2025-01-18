package com.ravijar.petstore.service;

import com.ravijar.petstore.model.Pet;
import com.ravijar.petstore.repository.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PetService {
    @Autowired
    private PetRepository petRepository;

    public List<Pet> getAllPets() {
        return petRepository.findAll();
    }

    public Optional<Pet> getPetById(Long id) {
        return petRepository.findById(id);
    }

    public Pet savePet(Pet pet) {
        return petRepository.save(pet);
    }

    public void deletePet(Long id) {
        petRepository.deleteById(id);
    }

    public Optional<Pet> updatePet(Long id, Pet updatedPet) {
        return petRepository.findById(id).map(existingPet -> {
            existingPet.setName(updatedPet.getName());
            existingPet.setSpecies(updatedPet.getSpecies());
            return petRepository.save(existingPet);
        });
    }
}
