package com.khaphp.interactservice.dto.comment;

import com.khaphp.common.dto.foodrecipe.CookingRecipeDTOviewInOrtherEntity;
import com.khaphp.common.dto.usersystem.UserSystemDTOviewInOrtherEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CommentDTOviewAll {
    private String id;
    private Date createDate;
    private String body;
    private String parentCommentId;
    private int childCmtSize;
    private UserSystemDTOviewInOrtherEntity ownerV;
    private CookingRecipeDTOviewInOrtherEntity cookingRecipeV;
}
