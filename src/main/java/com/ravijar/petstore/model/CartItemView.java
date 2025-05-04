package com.ravijar.petstore.model;

import lombok.Data;

@Data
public class CartItemView {
    private String id;
    private String petName;
    private int quantity;
}
