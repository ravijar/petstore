package com.ravijar.petstore.model;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.spring.data.firestore.Document;
import lombok.Data;

@Data
@Document(collectionName = "users")
public class User {
    @DocumentId
    private String id;
    private String email;
    private String name;
    private String role;
}
