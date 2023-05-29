package com.builder.recipe;

import org.springframework.web.bind.annotation.*;

@RequestMapping("/recipe")
@RestController
public class RecipeController {

    private final RecipeService service;

    public RecipeController(RecipeService service) {
        this.service = service;
    }
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/get")
    public String getRecipe(@RequestParam("ingredients") String ingredients){

       return this.service.getMealIdea(ingredients);

    }
}
