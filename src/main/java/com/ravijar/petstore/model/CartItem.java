package com.ravijar.petstore.model;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.spring.data.firestore.Document;
import lombok.Data;

@Data
@Document(collectionName = "cartItems")
public class CartItem {
    @DocumentId
    private String id;
    private String userId;
    private String itemId;
}
