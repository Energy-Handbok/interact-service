package com.khaphp.interactservice.entity;

import com.khaphp.interactservice.constant.TypeInteract;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Interact {
    @Id
    @UuidGenerator
    @Column(columnDefinition = "VARCHAR(36)")
    private String id;

    @Column(columnDefinition = "VARCHAR(36)")
    private String cookingRecipeId;
    @Column(columnDefinition = "VARCHAR(36)")
    private String customerId;  //if type is share, customerId is the customer who shared by owner of recipe

    private int star;   //for vote

    @Enumerated(EnumType.STRING)
    private TypeInteract type;
}
