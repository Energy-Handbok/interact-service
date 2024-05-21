package com.khaphp.interactservice.util.valid.TypeInteract;

import com.khaphp.common.constant.StatusCookingRecipe;
import com.khaphp.interactservice.constant.TypeInteract;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class ValidTypeInteractValidator implements ConstraintValidator<ValidTypeInteract, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        List<String> roles = List.of(TypeInteract.LIKE.toString(), TypeInteract.SHARE.toString(), TypeInteract.VOTE.toString(), TypeInteract.REPORT.toString(), StatusCookingRecipe.PRIVATE.toString(), StatusCookingRecipe.BAN.toString());
        if(roles.contains(value)){
            return true;
        }else{
            return false;
        }
    }
}
