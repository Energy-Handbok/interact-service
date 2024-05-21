package com.khaphp.interactservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Comment {
    @Id
    @UuidGenerator
    @Column(columnDefinition = "VARCHAR(36)")
    private String id;
    private Date createDate;
    private String body;
    @Column(columnDefinition = "VARCHAR(36)")
    private String parentCommentId;

    @Column(columnDefinition = "VARCHAR(36)")
    private String cookingRecipeId;

    @Column(columnDefinition = "VARCHAR(36)")
    private String ownerId;

    @Column(columnDefinition = "VARCHAR(36)")
    private String replyTo; //reply to another user
}
