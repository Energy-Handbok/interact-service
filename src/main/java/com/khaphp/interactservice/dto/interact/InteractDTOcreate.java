package com.khaphp.interactservice.dto.interact;

import com.khaphp.interactservice.util.valid.TypeInteract.ValidTypeInteract;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InteractDTOcreate {
    private String customerId;
    private String cookingRecipeId;
    @ValidTypeInteract
    private String typeInteract;
    private int star;
    private List<String> gmails;
}
