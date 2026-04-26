package org.example.securin.assembler;

import org.example.securin.controller.RecipeController;
import org.example.securin.dto.RecipeOutDTO;
import org.example.securin.model.Recipe;
import org.jspecify.annotations.NullMarked;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

//@Getter
//@Setter
//@NullMarked
//public class RecipeModel extends RepresentationModel<RecipeModel> {
//    private Integer id;
//    private String cuisine;
//    private String title;
//    private Float rating;
//    private Integer prep_time;
//    private Integer cook_time;
//    private Integer total_time;
//    private String description;
//    private JsonNode nutrients;
//    private String serves;
//}


//@Component
//@NullMarked
//public class RecipeModelAssembler extends RepresentationModelAssemblerSupport<Recipe, RecipeModel> {
//    public RecipeModelAssembler() {
//        super(RecipeController.class, RecipeModel.class);
//    }
//
//
//    @Override
//    public RecipeModel toModel(Recipe entity) {
//        RecipeModel model = new RecipeModel();
//        // Both RecipeModel and Recipe have the same property names. So copy the values from the Entity to the Model
//        BeanUtils.copyProperties(entity, model);
//        return model;
//    }
//}

@Component
@NullMarked
public class RecipeModelAssembler extends RepresentationModelAssemblerSupport<Recipe, EntityModel<RecipeOutDTO>> {

    // Hardcoded the controller and resource type
    @SuppressWarnings("unchecked")
    public RecipeModelAssembler() {
        super(RecipeController.class, (Class<EntityModel<RecipeOutDTO>>) (Class<?>) EntityModel.class);
    }

    @Override
    public EntityModel<RecipeOutDTO> toModel(Recipe entity) {
        RecipeOutDTO dto = RecipeOutDTO.builder()
                .id(entity.getId())
                .cuisine(entity.getCuisine())
                .title(entity.getTitle())
                .rating(entity.getRating())
                .prep_time(entity.getPrep_time())
                .cook_time(entity.getCook_time())
                .total_time(entity.getTotal_time())
                .description(entity.getDescription())
                .nutrients(entity.getNutrients())
                .serves(entity.getServes())
                .build();

        return EntityModel.of(dto);
    }
}