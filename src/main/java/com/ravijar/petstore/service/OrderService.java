package com.ravijar.petstore.service;

import com.google.cloud.Timestamp;
import com.ravijar.petstore.model.CartItem;
import com.ravijar.petstore.model.OrderView;
import com.ravijar.petstore.model.Order;
import com.ravijar.petstore.model.Item;
import com.ravijar.petstore.repository.OrderRepository;
import com.ravijar.petstore.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private ItemRepository itemRepository;

    public Mono<Order> placeOrder(String userId) {
        return cartService.getCartForUser(userId)
                .collectList()
                .flatMap(cartItems -> {
                    if (cartItems.isEmpty()) {
                        return Mono.error(new RuntimeException("Cart is empty"));
                    }

                    // Group and count quantities per item
                    Map<String, Long> itemIdToQuantity = cartItems.stream()
                            .collect(Collectors.groupingBy(CartItem::getItemId, Collectors.counting()));

                    // Step 1: Verify all items have enough stock
                    return Flux.fromIterable(itemIdToQuantity.entrySet())
                            .flatMap(entry ->
                                    itemRepository.findById(entry.getKey())
                                            .switchIfEmpty(Mono.error(new RuntimeException("Item not found: " + entry.getKey())))
                                            .flatMap(item -> {
                                                long requestedQty = entry.getValue();
                                                if (item.getStock() < requestedQty) {
                                                    return Mono.error(new RuntimeException("Insufficient stock for item: " + item.getName()));
                                                }
                                                return Mono.just(item);
                                            })
                            )
                            .collectList()
                            .flatMap(itemsToUpdate -> {
                                // Step 2: Decrement stock and save
                                return Flux.fromIterable(itemsToUpdate)
                                        .flatMap(item -> {
                                            long qty = itemIdToQuantity.get(item.getId());
                                            item.setStock(item.getStock() - (int) qty);
                                            return itemRepository.save(item);
                                        })
                                        .then(
                                                // Step 3: Save the order
                                                Mono.defer(() -> {
                                                    Order order = new Order();
                                                    order.setUserId(userId);
                                                    order.setItemIds(cartItems.stream().map(CartItem::getItemId).toList());
                                                    order.setOrderTime(Timestamp.now());
                                                    order.setStatus("PLACED");
                                                    return orderRepository.save(order)
                                                            .flatMap(savedOrder -> cartService.clearCart(userId).thenReturn(savedOrder));
                                                })
                                        );
                            });
                });
    }


    public Flux<Order> getOrdersByUserId(String userId) {
        return orderRepository.findByUserId(userId);
    }

    public Mono<OrderView> toOrderView(Order order) {
        return Flux.fromIterable(order.getItemIds())
                .flatMap(itemId -> itemRepository.findById(itemId))
                .collectList()
                .map(items -> {
                    // Count items
                    Map<String, Long> itemCountMap = items.stream()
                            .collect(Collectors.groupingBy(Item::getName, Collectors.counting()));

                    // Calculate total
                    double total = items.stream()
                            .mapToDouble(Item::getPrice)
                            .sum();

                    // Format order time
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm:ss a")
                            .withZone(ZoneId.systemDefault());
                    String formattedTime = formatter.format(order.getOrderTime().toSqlTimestamp().toInstant());

                    // Build OrderView
                    OrderView view = new OrderView();
                    view.setId(order.getId());
                    view.setStatus(order.getStatus());
                    view.setOrderTime(formattedTime);
                    view.setTotal(total);
                    view.setItemSummary(
                            itemCountMap.entrySet().stream()
                                    .map(e -> e.getKey() + " x " + e.getValue())
                                    .toList()
                    );
                    return view;
                });
    }

}

