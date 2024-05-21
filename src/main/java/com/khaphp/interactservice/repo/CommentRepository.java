package com.khaphp.interactservice.repo;

import com.khaphp.interactservice.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {
    Page<Comment> findAllByCookingRecipeId(String cookingRecipeId, Pageable pageable);

    int countAllByCookingRecipeIdAndParentCommentId(String cookingRecipeId, String parentCommentId);

    Page<Comment> findAllByCookingRecipeIdAndParentCommentIdOrderByCreateDate(String cookingRecipeId, String parentCommentId, Pageable pageable);

    @Query("select c from Comment c where c.cookingRecipeId = ?1 and c.parentCommentId = ''")
    Page<Comment> findAllCommentByCookingRecipeId(String cookingRecipeId, PageRequest of);

    @Query("select c from Comment c where c.parentCommentId = ''")
    Page<Comment> findAllComment(PageRequest of);
}
