package com.ravijar.petstore.controller;

import com.ravijar.petstore.model.User;
import com.ravijar.petstore.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public Mono<ResponseEntity<User>> login(@RequestHeader("Authorization") String token) {
        if (token == null) {
            return Mono.just(ResponseEntity.badRequest().build());
        }

        return authService.authenticateAndSaveIfNeeded(token)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }
}
