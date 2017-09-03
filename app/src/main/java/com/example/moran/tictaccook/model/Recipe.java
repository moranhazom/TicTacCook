package com.example.moran.tictaccook.model;

/**
 * Created by moran on 22/07/2017.
 */

public class Recipe {

    private String id;
    private String name;
    private boolean checked;
    private String imageUrl;
    private String ingredients;
    private String method;
    private String preparationTime;
    private double lastUpdateDate;
    private boolean archive;

    public Recipe() {
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isChecked() {
        return checked;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getIngredients() {
        return ingredients;
    }

    public String getMethod() {
        return method;
    }

    public String getPreparationTime() {
        return preparationTime;
    }

    public double getLastUpdateDate() {
        return lastUpdateDate;
    }

    public boolean isArchive() {
        return archive;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setPreparationTime(String preparationTime) {
        this.preparationTime = preparationTime;
    }

    public void setLastUpdateDate(double lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public void setArchive(boolean archive) {
        this.archive = archive;
    }

}
