package com.khaphp.interactservice.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CommentDTOcreate {
    private String cookingRecipeId;
    private String ownerId;
    private String replyToId;
    private String body;
    private String parentCommentId;
}
