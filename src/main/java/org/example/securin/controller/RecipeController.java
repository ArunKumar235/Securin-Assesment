package org.example.securin.controller;

import lombok.RequiredArgsConstructor;
import org.example.securin.assembler.RecipeModelAssembler;
import org.example.securin.dto.RecipeOutDTO;
import org.example.securin.model.Recipe;
import org.example.securin.service.RecipeService;
import org.example.securin.specification.RecipeSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService service;

    @GetMapping("/")
    public PagedModel<EntityModel<RecipeOutDTO>> getRecipes(
//            @RequestParam(name = "page", defaultValue = "0")int page,
//            @RequestParam(name = "size", defaultValue = "10")int size,
//            @RequestParam(name = "sortBy", defaultValue = "rating") String sortBy,
//            @RequestParam(name = "order", defaultValue = "desc") String order,
            Pageable pageable,
            PagedResourcesAssembler<Recipe> pagedResourcesAssembler,
            RecipeModelAssembler recipeModelAssembler
    ) {
            Page<Recipe> recipePage = service.getAllRecipes(pageable);
//        Page<Recipe> recipePage = service.getAllRecipes(page, size, sortBy, order);
        //  PagedResourcesAssembler<Recipe>: This is a Spring HATEOAS utility. Its only job is to take a Spring Data Page and wrap it in a PagedModel. It calculates the _links (next, prev, etc.) based on the page metadata.
        //  RecipeModelAssembler: This is your custom class (extending RepresentationModelAssembler). Its job is to convert a single Recipe entity into a RecipeOutDTO (RecipeModel).
        return pagedResourcesAssembler.toModel(recipePage, recipeModelAssembler);
    }

    @GetMapping("/search")
    public PagedModel<EntityModel<RecipeOutDTO>> searchRecipes(
        @RequestParam(required = false) String calories,
        @RequestParam(required = false) String title,
        @RequestParam(required = false) String cuisine,
        @RequestParam(required = false) String total_time,
        @RequestParam(required = false) String rating,
        Pageable pageable,
        PagedResourcesAssembler<Recipe> pagedResourcesAssembler,
        RecipeModelAssembler recipeModelAssembler
    ) {
        Specification<Recipe> specification = Specification.where(RecipeSpecification.hasTitle(title))
                .and(RecipeSpecification.hasCuisine(cuisine))
                .and(RecipeSpecification.withCalories(calories))
                .and(RecipeSpecification.withNumericFilter("total_time", total_time))
                .and(RecipeSpecification.withNumericFilter("rating", rating));

         Page<Recipe> recipePage = service.searchRecipes(specification, pageable);

         return pagedResourcesAssembler.toModel(recipePage, recipeModelAssembler);
    }

    @PostMapping("/upload")
    public String uploadRecipes(
            @RequestParam(name = "file") MultipartFile file
    ) {
        try {
            service.loadRecipes(file.getInputStream());
            return "Data inserted successfully";
        } catch (Exception e) {
            return "Failed: " + e.getMessage();
        }
    }

}
