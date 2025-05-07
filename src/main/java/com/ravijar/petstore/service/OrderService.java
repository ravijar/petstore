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
        return cartService.getCartForUser(userId).collectList()
                .flatMap(cartItems -> {
                    if (cartItems.isEmpty()) return Mono.error(new RuntimeException("Cart is empty"));

                    Order order = new Order();
                    order.setUserId(userId);
                    order.setItemIds(cartItems.stream().map(CartItem::getItemId).toList());
                    order.setOrderTime(Timestamp.now());
                    order.setStatus("PLACED");

                    return orderRepository.save(order)
                            .flatMap(savedOrder -> cartService.clearCart(userId).thenReturn(savedOrder));
                });
    }

    public Flux<Order> getOrdersByUserId(String userId) {
        return orderRepository.findByUserId(userId);
    }

    public Mono<OrderView> toOrderView(Order order) {
        return Flux.fromIterable(order.getItemIds())
                .flatMap(itemId -> itemRepository.findById(itemId))
                .collect(Collectors.groupingBy(Item::getName, Collectors.counting()))
                .map(itemCountMap -> {
                    OrderView view = new OrderView();
                    view.setId(order.getId());
                    view.setStatus(order.getStatus());

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm:ss a")
                            .withZone(ZoneId.systemDefault());
                    String formattedTime = formatter.format(order.getOrderTime().toSqlTimestamp().toInstant());
                    view.setOrderTime(formattedTime);

                    view.setItemSummary(
                            itemCountMap.entrySet().stream()
                                    .map(e -> e.getKey() + " x " + e.getValue())
                                    .toList()
                    );
                    return view;
                });
    }

}

