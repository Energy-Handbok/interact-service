package com.khaphp.interactservice.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CommentDTOviewDetail {
    private int ortherChildCmtSize;
    private List<CommentChild> commentChildrens;
}
