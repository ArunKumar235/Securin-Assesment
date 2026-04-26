package org.example.securin.repo;

import org.example.securin.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RecipeRepository extends JpaRepository<Recipe, Integer>, JpaSpecificationExecutor<Recipe> {

}
