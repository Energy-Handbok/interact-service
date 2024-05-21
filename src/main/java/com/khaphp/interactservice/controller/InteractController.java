package com.khaphp.interactservice.controller;

import com.khaphp.common.dto.ResponseObject;
import com.khaphp.interactservice.dto.interact.InteractDTOcreate;
import com.khaphp.interactservice.dto.interact.InteractDTOdelete;
import com.khaphp.interactservice.service.InteractService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/interact")
//@SecurityRequirement(name = "EnergyHandbook")
@RequiredArgsConstructor
public class InteractController {
    private final InteractService interactService;

    @PostMapping
    @Operation(description = "LIKE, VOTE, REPORT -> customerID là thàng cus đang làm vc dó             \n" +
            "SHARE -> customerId là thằng Owner của cái cooking recipe đó, gmails là nơi chứa các email để cho thằng owner share           \n" +
            "VOTE -> cần the star để hiê số sao             \n" +
            "LIKE, REPORT (customerId, cookingRecipeId, typeInteract)           \n" +
            "VOTE (customerId, cookingRecipeId, typeInteract, star)           \n" +
            "SHARE (customerId, cookingRecipeId, typeInteract, gmails)")
    public ResponseEntity<Object> createObject(@RequestBody @Valid InteractDTOcreate object){
        ResponseObject<Object> responseObject = interactService.create(object);
        if(responseObject.getCode() == 200){
            return ResponseEntity.ok(responseObject);
        }
        return ResponseEntity.badRequest().body(responseObject);
    }

    @DeleteMapping
    @Operation(description = "LIKE, REPORT, VOTE (customerId, cookingRecipeId, typeInteract)              \n" +
            "SHARE (customerId, cookingRecipeId, typeInteract, ownerId) -> owner là thằng chủ, cusId là thằng customer cần xóa share")
    public ResponseEntity<Object> deleteObject(@RequestBody InteractDTOdelete object){
        ResponseObject<Object> responseObject = interactService.delete(object.getId());
        if(responseObject.getCode() == 200){
            return ResponseEntity.ok(responseObject);
        }
        return ResponseEntity.badRequest().body(responseObject);
    }
}
