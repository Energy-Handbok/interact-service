package com.khaphp.interactservice.service;


import com.khaphp.common.dto.ResponseObject;
import com.khaphp.interactservice.dto.comment.CommentDTOcreate;

public interface CommentService {

    ResponseObject<Object> getAll(int pageSize, int pageIndex, String cookingRecipeId);
    ResponseObject<Object> getChildComment(String id, int pageSize, int pageIndex);
    ResponseObject<Object> create(CommentDTOcreate object);
//    ResponseObject<Object> update(CommentDTOupdate object);
    ResponseObject<Object> delete(String id);
}
