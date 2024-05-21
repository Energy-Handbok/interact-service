package com.khaphp.interactservice.service;

import com.khaphp.common.dto.ResponseObject;
import com.khaphp.common.dto.foodrecipe.CookingRecipeDTOviewInOrtherEntity;
import com.khaphp.common.dto.noti.NotificationDTOcreate;
import com.khaphp.common.dto.usersystem.UserSystemDTOviewInOrtherEntity;
import com.khaphp.common.entity.CookingRecipe;
import com.khaphp.common.entity.UserSystem;
import com.khaphp.interactservice.call.foodrecipeservice.FoodRecipeServiceCall;
import com.khaphp.interactservice.call.notiservice.NotiServiceCall;
import com.khaphp.interactservice.call.userservice.UserServiceCall;
import com.khaphp.interactservice.dto.comment.CommentChild;
import com.khaphp.interactservice.dto.comment.CommentDTOcreate;
import com.khaphp.interactservice.dto.comment.CommentDTOviewAll;
import com.khaphp.interactservice.dto.comment.CommentDTOviewDetail;
import com.khaphp.interactservice.entity.Comment;
import com.khaphp.interactservice.exception.ObjectNotFound;
import com.khaphp.interactservice.repo.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {
    public static final String OBJECT_NOT_FOUND = "object not found";
    public static final String SUCCESS = "Success";
    public static final String EXCEPTION_MSG = "Exception: ";
    private final CommentRepository commentRepository;
    private final UserServiceCall userServiceCall;
    private final FoodRecipeServiceCall foodRecipeServiceCall;
    private final ModelMapper modelMapper;
    private final NotiServiceCall notiServiceCall;

    @Override
    public ResponseObject<Object> getAll(int pageSize, int pageIndex, String cookingRecipeId) {
        Page<Comment> objListPage = null;
        List<Comment> objList = null;
        int totalPage = 0;
        //paging
        if(pageSize > 0 && pageIndex > 0){
            if(!cookingRecipeId.equals("")){
                objListPage = commentRepository.findAllCommentByCookingRecipeId(cookingRecipeId, PageRequest.of(pageIndex - 1, pageSize));
            }else{
                objListPage = commentRepository.findAllComment(PageRequest.of(pageIndex - 1, pageSize));
            }
            if(objListPage != null){
                totalPage = objListPage.getTotalPages();
                objList = objListPage.getContent();
            }
        }else{ //get all
            objList = commentRepository.findAll();
            pageIndex = 1;
        }
        List<CommentDTOviewAll> objListDTO = new ArrayList<>();
        if(objList != null) {
            objList.forEach(object -> {
                CommentDTOviewAll objectView = modelMapper.map(object, CommentDTOviewAll.class);
                objectView.setChildCmtSize(commentRepository.countAllByCookingRecipeIdAndParentCommentId(object.getCookingRecipeId(), object.getId()));
                //get owner
                UserSystem owner = userServiceCall.getObject(object.getOwnerId());
                objectView.setOwnerV(UserSystemDTOviewInOrtherEntity.builder()
                        .id(owner.getId())
                        .name(owner.getName())
                        .imgUrl(owner.getImgUrl()).build());
                //get cooking recipe
                CookingRecipe cookingRecipe = foodRecipeServiceCall.getDetail(object.getCookingRecipeId());
                objectView.setCookingRecipeV(CookingRecipeDTOviewInOrtherEntity.builder()
                        .id(cookingRecipe.getId())
                        .name(cookingRecipe.getName())
                        .build());
                objListDTO.add(objectView);
            });
        }

        return ResponseObject.builder()
                .code(200).message(SUCCESS)
                .pageSize(objListDTO.size()).pageIndex(pageIndex).totalPage(totalPage)
                .data(objListDTO)
                .build();
    }

    /**
     * take the number of child cmt and return (the return include the number of rest child cmt)
     */
    @Override
    public ResponseObject<Object> getChildComment(String id, int pageSize, int pageIndex) {
        try{
            Comment object = commentRepository.findById(id).orElse(null);
            if(object == null) {
                throw new ObjectNotFound(OBJECT_NOT_FOUND);
            }
            CommentDTOviewDetail objectView = modelMapper.map(object, CommentDTOviewDetail.class);

            Page<Comment> listChildPage = commentRepository.findAllByCookingRecipeIdAndParentCommentIdOrderByCreateDate(object.getCookingRecipeId(), object.getId(), PageRequest.of(0, pageSize * pageIndex));
            int pagesize = 0;
            //calculate number of rest other child cmt (tính xem co la bao nhiêu cmt con)
            if(listChildPage.getTotalElements() > 0){
                List<Comment> listChild = listChildPage.getContent();
                pagesize = listChild.size();
                //set orther child cmt còn lại
                if(listChildPage.getTotalElements() == listChild.size()){
                    objectView.setOrtherChildCmtSize(0);
                }else if(listChildPage.getTotalElements() > listChild.size()){
                    objectView.setOrtherChildCmtSize((int) (listChildPage.getTotalElements() - listChild.size()));
                }

                //map data
                objectView.setCommentChildrens(new ArrayList<>());
                listChild.forEach(x -> {
                    CommentChild objectChild = modelMapper.map(x, CommentChild.class);

                    //take cooking recipe
                    CookingRecipe cookingRecipe = foodRecipeServiceCall.getDetail(x.getCookingRecipeId());
                    objectChild.setCookingRecipeV(CookingRecipeDTOviewInOrtherEntity.builder()
                            .id(x.getCookingRecipeId())
                            .name(cookingRecipe.getName())
                            .build());

                    //get owner
                    UserSystem owner = userServiceCall.getObject(x.getOwnerId());
                    objectChild.setOwnerV(UserSystemDTOviewInOrtherEntity.builder()
                            .id(owner.getId())
                            .name(owner.getName())
                            .imgUrl(owner.getImgUrl())
                            .build());

                    //get user have been reply by owner
                    UserSystem replyTo = userServiceCall.getObject(x.getReplyTo());
                    objectChild.setReplyTo(UserSystemDTOviewInOrtherEntity.builder()
                            .id(replyTo.getId())
                            .name(replyTo.getName())
                            .imgUrl(replyTo.getImgUrl())
                            .build());
                    objectView.getCommentChildrens().add(objectChild);
                });
            }
            return ResponseObject.builder()
                    .code(200).message("Found")
                    .data(objectView)
                    .pageSize(pagesize)
                    .build();
        }catch (Exception e){
            return ResponseObject.builder()
                    .code(400).message(EXCEPTION_MSG + e.getMessage())
                    .build();
        }
    }

    /**
     * create cmt and notify if someone answer, reply the owner of the recipe
     */
    @Override
    public ResponseObject<Object> create(CommentDTOcreate object) {
        try{
            UserSystem owner = userServiceCall.getObject(object.getOwnerId());
            if(owner == null){
                throw new ObjectNotFound("owner not found");
            }

            UserSystem replyTo = userServiceCall.getObject(object.getReplyToId());
            if(replyTo == null){
                throw new ObjectNotFound("replyTo user not found");
            }

            CookingRecipe cookingRecipe = foodRecipeServiceCall.getDetail(object.getCookingRecipeId());
            if(cookingRecipe == null){
                throw new ObjectNotFound("cookingRecipe not found");
            }

            Comment comment = modelMapper.map(object, Comment.class);
            comment.setOwnerId(owner.getId());
            comment.setReplyTo(replyTo.getId());
            comment.setCookingRecipeId(cookingRecipe.getId());
            comment.setCreateDate(new Date(System.currentTimeMillis()));
            if(comment.getParentCommentId() == null){
                comment.setParentCommentId("");
            }
            commentRepository.save(comment);

            //notify if customer comment answer owner of cooking recipe
            try{
                if(cookingRecipe.getCustomerId().equals(replyTo.getId())){
                    notiServiceCall.create(NotificationDTOcreate.builder()
                            .userId(cookingRecipe.getCustomerId())
                            .title(owner.getName() +" đã bình luận vào công thức " + cookingRecipe.getName())
                            .build());
                }
            }catch (Exception e){
                log.error("Error notify for owner of cooking recipe when someone cmt to it: " + e.getMessage());
            }
            return ResponseObject.builder()
                    .code(200)
                    .message(SUCCESS)
                    .data(comment)
                    .build();
        }catch (Exception e){
            return ResponseObject.builder()
                    .code(400)
                    .message(EXCEPTION_MSG + e.getMessage())
                    .build();
        }
    }

    /**
     * if cmt has child cmt -> change body of parents cmt to "[ Bình luận này đã bị xóa ]"
     * if cmt not has child cmt -> delete
     */
    @Override
    public ResponseObject<Object> delete(String id) {
        try{
            Comment object = commentRepository.findById(id).orElse(null);
            if(object == null) {
                throw new ObjectNotFound(OBJECT_NOT_FOUND);
            }
            if(commentRepository.countAllByCookingRecipeIdAndParentCommentId(object.getCookingRecipeId(), object.getId()) > 0){
                object.setBody("[ Bình luận này đã bị xóa ]");
                commentRepository.save(object);
            }else{
                commentRepository.delete(object);
            }
            return ResponseObject.builder()
                    .code(200)
                    .message(SUCCESS)
                    .build();
        }catch (Exception e){
            return ResponseObject.builder()
                    .code(400)
                    .message(EXCEPTION_MSG + e.getMessage())
                    .build();
        }
    }
}
