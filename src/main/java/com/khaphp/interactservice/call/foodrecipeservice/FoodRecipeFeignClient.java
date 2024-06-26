package com.khaphp.interactservice.call.foodrecipeservice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "food-recipe-service")
public interface FoodRecipeFeignClient {
    @GetMapping("/api/v1/cooking-recipe/detail")
    public ResponseEntity<Object> getDetail(@RequestParam String id);
}
