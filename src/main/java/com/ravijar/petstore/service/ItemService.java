package com.ravijar.petstore.service;

import com.ravijar.petstore.model.Item;
import com.ravijar.petstore.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ItemService {
    @Autowired
    private ItemRepository itemRepository;

    public Flux<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Mono<Item> getItemById(String id) {
        return itemRepository.findById(id);
    }

    public Mono<Item> saveItem(Item item) {
        return itemRepository.save(item);
    }

    public Mono<Void> deleteItem(String id) {
        return itemRepository.deleteById(id);
    }

    public Mono<Item> updateItem(String id, Item updatedItem) {
        return itemRepository.findById(id)
                .flatMap(existingItem -> {
                    existingItem.setName(updatedItem.getName());
                    existingItem.setCategory(updatedItem.getCategory());
                    existingItem.setDescription(updatedItem.getDescription());
                    existingItem.setImageURL(updatedItem.getImageURL());
                    existingItem.setPrice(updatedItem.getPrice());
                    existingItem.setStock(updatedItem.getStock());
                    return itemRepository.save(existingItem);
                });
    }
}
