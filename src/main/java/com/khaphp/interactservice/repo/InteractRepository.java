package com.khaphp.interactservice.repo;

import com.khaphp.interactservice.constant.TypeInteract;
import com.khaphp.interactservice.entity.Interact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InteractRepository extends JpaRepository<Interact, String> {
    Interact findByCustomerIdAndCookingRecipeIdAndType(String customerId, String cookingRecipeId, TypeInteract type);
    boolean existsByCustomerIdAndCookingRecipeIdAndType(String customerId, String cookingRecipeId, TypeInteract type);
}
