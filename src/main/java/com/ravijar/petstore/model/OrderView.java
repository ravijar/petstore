package com.ravijar.petstore.model;

import lombok.Data;

import java.util.List;

@Data
public class OrderView {
    private String id;
    private String orderTime;
    private List<String> itemSummary;
    private String status;
    private double total;
}

