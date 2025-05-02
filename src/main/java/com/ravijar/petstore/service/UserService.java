package com.ravijar.petstore.service;

import com.ravijar.petstore.model.User;
import com.ravijar.petstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Flux<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Mono<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    public Mono<User> saveUser(User user) {
        return userRepository.save(user);
    }

    public Mono<Void> deleteUser(String id) {
        return userRepository.deleteById(id);
    }

    public Mono<User> updateUser(String id, User updatedUser) {
        return userRepository.findById(id)
                .flatMap(existingUser -> {
                    existingUser.setEmail(updatedUser.getEmail());
                    existingUser.setName(updatedUser.getName());
                    existingUser.setRole(updatedUser.getRole());
                    return userRepository.save(existingUser);
                });
    }
}
