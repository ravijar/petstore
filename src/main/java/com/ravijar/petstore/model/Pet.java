package com.ravijar.petstore.model;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.spring.data.firestore.Document;
import lombok.Data;

@Data
@Document(collectionName = "pets")
public class Pet {
    @DocumentId
    private String id;

    private String name;
    private String species;
    private String description;
    private String imageURL;
}
