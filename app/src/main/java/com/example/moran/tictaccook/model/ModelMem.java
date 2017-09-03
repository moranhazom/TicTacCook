package com.example.moran.tictaccook.model;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by moran on 01/08/2017.
 */

public class ModelMem {

    private List<Recipe> data = new LinkedList<>();

    ModelMem(){
        for(int i = 0; i<20; i++) {
            Recipe recipe = new Recipe();
            recipe.setId("" + i);
            recipe.setName("recipe " + i);
            recipe.setChecked(false);
            recipe.setImageUrl("");
            recipe.setIngredients("Default ingredients");
            recipe.setMethod("Default directions");
            recipe.setPreparationTime("10 Mins");
            data.add(recipe);
        }
    }

    public List<Recipe> getAllRecipes(){
        return data;
    }

    public void addRecipe(Recipe recipe){
        data.add(recipe);
    }

    public Recipe getRecipe(String RecipeId) {
        for (Recipe r : data) {
            if (r.getId().equals(RecipeId)) {
                return r;
            }
        }
        return null;
    }

    public void deleteRecipe(String recipeId) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getId().equals(recipeId)) {
                data.remove(i);
                return;
            }
        }
    }

    public int getIndex(Recipe recipe){
        return data.indexOf(recipe);
    }

    public boolean checkIfIdAlreadyExists(String recipetId, int index) {
        for (Recipe r : data){
            if (r.getId().equals(recipetId))
                if (data.indexOf(r) != index)
                    return false;
        }
        return true; //true means-no Exists
    }

}
