package com.ravijar.petstore.model;

import lombok.Data;

import java.util.List;

@Data
public class OrderView {
    private String id;
    private String orderTime;
    private List<String> petSummary;
    private String status;
}

