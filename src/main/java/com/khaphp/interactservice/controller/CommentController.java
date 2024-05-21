package com.khaphp.interactservice.controller;

import com.khaphp.common.dto.ResponseObject;
import com.khaphp.interactservice.dto.comment.CommentDTOcreate;
import com.khaphp.interactservice.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/comment")
//@SecurityRequirement(name = "EnergyHandbook")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestParam(defaultValue = "10") int pageSize,
                                    @RequestParam(defaultValue = "1") int pageIndex,
                                    @RequestParam(defaultValue = "") String cookingRecipeId){
        ResponseObject<Object> responseObject = commentService.getAll(pageSize, pageIndex, cookingRecipeId);
        if(responseObject.getCode() == 200){
            return ResponseEntity.ok(responseObject);
        }
        return ResponseEntity.badRequest().body(responseObject);
    }
    @GetMapping("/child-comment")
    @Operation(description = "load cmt, giống trong tiktok ấy, vd: pagesize=5, pageindex=1, thì lúc đầu load lần dầu là 5 item, nếu load lần 2 hay (pageindex=2) thì số lượng trả về là 10 item (5 của lần dầu + 5 của lần sau), tương tự nếu lần 3 là 15, 4 là 20, ...")
    public ResponseEntity<Object> getObject(@Parameter(description = "số lượng load mỗi lần tăng lên bao nhiêu") @RequestParam(defaultValue = "5") int pageSize,
                                       @Parameter(description = "số lần load") @RequestParam(defaultValue = "1") int pageIndex,
                                       @RequestParam(defaultValue = "") String id){
        ResponseObject<Object> responseObject = commentService.getChildComment(id, pageSize, pageIndex);
        if(responseObject.getCode() == 200){
            return ResponseEntity.ok(responseObject);
        }
        return ResponseEntity.badRequest().body(responseObject);
    }

    @PostMapping
    @Operation(description = "- owner là người viết cmt đó nha            \n" +
            "- cookingRecipeId là chỉ id của cái cooking recipe mà mình cmt ở dưới                  \n" +
            "- parentCommentId chứa id của 1 cmt parent ấy, giống tiktok ấy, vd: A cmt in Recipe, B reply A (hay lúc này A cmt sẽ là parent cmt và B cmt sẽ là child của nó) " +
            "-> lúc naỳ tất ca các reply trong phần A child này (cho dù nó ko reply chính thằng A cmt, nó reply thằng B cmt trong A child đó) -> thì nó vẫn mang parentCommentId là A cmt, hay nó là tối cao")
    public ResponseEntity<Object> createObject(@RequestBody @Valid CommentDTOcreate object){
        ResponseObject<Object> responseObject = commentService.create(object);
        if(responseObject.getCode() == 200){
            return ResponseEntity.ok(responseObject);
        }
        return ResponseEntity.badRequest().body(responseObject);
    }

    @DeleteMapping
    public ResponseEntity<Object> deleteObject(String id){
        ResponseObject<Object> responseObject = commentService.delete(id);
        if(responseObject.getCode() == 200){
            return ResponseEntity.ok(responseObject);
        }
        return ResponseEntity.badRequest().body(responseObject);
    }
}
