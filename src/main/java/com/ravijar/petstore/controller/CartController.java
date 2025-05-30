package com.ravijar.petstore.controller;

import com.ravijar.petstore.model.CartItem;
import com.ravijar.petstore.model.CartItemView;
import com.ravijar.petstore.service.AuthService;
import com.ravijar.petstore.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private AuthService authService;

    @PostMapping
    public Mono<CartItem> addToCart(@RequestHeader("Authorization") String token, @RequestBody CartItem item) {
        return authService.extractUserId(token)
                .flatMap(userId -> cartService.addToCart(userId, item));
    }

    @GetMapping
    public Flux<CartItemView> getCart(@RequestHeader("Authorization") String token) {
        return authService.extractUserId(token)
                .flatMapMany(cartService::getCartViewForUser);
    }

    @DeleteMapping("/{cartItemId}")
    public Mono<ResponseEntity<Void>> removeFromCart(@RequestHeader("Authorization") String token,
                                                     @PathVariable String cartItemId) {
        return authService.extractUserId(token)
                .flatMap(userId ->
                        cartService.getCartForUser(userId)
                                .filter(cartItem -> cartItem.getId().equals(cartItemId))
                                .next()
                                .flatMap(cartItem -> cartService.removeFromCart(cartItemId)
                                        .thenReturn(ResponseEntity.noContent().<Void>build()))
                )
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }

}

