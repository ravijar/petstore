package com.ravijar.petstore.controller;

import com.ravijar.petstore.model.Pet;
import com.ravijar.petstore.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/pets")
public class PetController {
    @Autowired
    private PetService petService;

    @GetMapping
    public Flux<Pet> getAllPets() {
        return petService.getAllPets();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Pet>> getPetById(@PathVariable String id) {
        return petService.getPetById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Pet>> createPet(@RequestBody Pet pet) {
        return petService.savePet(pet)
                .map(savedPet -> ResponseEntity.status(HttpStatus.CREATED).body(savedPet));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Pet>> updatePet(@PathVariable String id, @RequestBody Pet updatedPet) {
        return petService.updatePet(id, updatedPet)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deletePet(@PathVariable String id) {
        return petService.getPetById(id)
                .flatMap(existingPet ->
                        petService.deletePet(id).thenReturn(ResponseEntity.noContent().<Void>build()))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
