package com.ravijar.petstore.model;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.spring.data.firestore.Document;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Document(collectionName = "orders")
public class Order {
    @DocumentId
    private String id;
    private String userId;
    private List<String> petIds;
    private Instant orderTime;
    private String status;
}

