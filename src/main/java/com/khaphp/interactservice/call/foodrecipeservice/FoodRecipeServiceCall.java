package com.khaphp.interactservice.call.foodrecipeservice;

import com.khaphp.common.entity.CookingRecipe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.LinkedHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class FoodRecipeServiceCall {
    public static final String CALL_OTHER_SERVICE_ERROR = "Call other service error: {}";
    public static final String CALL_OTHER_SERVICE = "Call other service";
    private final FoodRecipeFeignClient foodRecipeFeignClient;
    private final CircuitBreakerFactory circuitBreakerFactory;
    public CookingRecipe getDetail(String id) {
        String where = "[CookingRecipe getDetail]";
        return (CookingRecipe) circuitBreakerFactory.create("getdetailCookingRecipe").run(
                () -> {
                    log.info(where + CALL_OTHER_SERVICE);
                    ResponseEntity<?> responseEntity = foodRecipeFeignClient.getDetail(id); //data: CookingRecipeDTOdetail
                    log.info("response [getObject]: " + responseEntity);
                    return getFoodRecipeFromResponse(responseEntity);
                },
                throwable -> {
                    log.error(where + CALL_OTHER_SERVICE_ERROR, throwable.getMessage());
                    return null;
                });
    }

    private CookingRecipe getFoodRecipeFromResponse(ResponseEntity<?> responseEntity) {
        CookingRecipe object = null;
        try {
            LinkedHashMap<String, Object> data = (LinkedHashMap<String, Object>) responseEntity.getBody();
            if(data == null) return null;
            object = CookingRecipe.getObjectFromLinkedHashMap((LinkedHashMap<String, Object>) data.get("data"));
        } catch (NullPointerException | ParseException e) {
            throw new RuntimeException(e);
        }
        return object;
    }
}
