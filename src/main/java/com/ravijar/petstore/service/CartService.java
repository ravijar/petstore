package com.ravijar.petstore.service;

import com.ravijar.petstore.model.CartItem;
import com.ravijar.petstore.model.CartItemView;
import com.ravijar.petstore.repository.CartItemRepository;
import com.ravijar.petstore.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ItemRepository itemRepository;

    public Flux<CartItem> getCartForUser(String userId) {
        return cartItemRepository.findByUserId(userId);
    }

    public Flux<CartItemView> getCartViewForUser(String userId) {
        return cartItemRepository.findByUserId(userId)
                .collectMultimap(CartItem::getItemId)
                .flatMapMany(grouped -> Flux.fromIterable(grouped.entrySet()))
                .flatMap(entry -> {
                    String itemId = entry.getKey();
                    int quantity = entry.getValue().size();
                    String anyCartItemId = entry.getValue().iterator().next().getId();

                    return itemRepository.findById(itemId)
                            .map(item -> {
                                CartItemView view = new CartItemView();
                                view.setId(anyCartItemId);
                                view.setItemName(item.getName());
                                view.setQuantity(quantity);
                                view.setTotal(quantity * item.getPrice());
                                return view;
                            });
                });
    }


    public Mono<CartItem> addToCart(String userId, CartItem item) {
        item.setId(null);
        item.setUserId(userId);
        return cartItemRepository.save(item);
    }

    public Mono<Void> clearCart(String userId) {
        return cartItemRepository.findByUserId(userId)
                .flatMap(cartItem -> cartItemRepository.deleteById(cartItem.getId()))
                .then();
    }

    public Mono<Void> removeFromCart(String cartItemId) {
        return cartItemRepository.deleteById(cartItemId);
    }

}

