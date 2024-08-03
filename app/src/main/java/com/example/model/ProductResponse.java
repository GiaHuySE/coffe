package com.example.model;

import java.util.List;
public class ProductResponse {
    private int id;
    private String name;
    private String price;
    private List<Recipe> recipes;
    private List<Promotion> promotions;

    public ProductResponse(int id, String name, String price, List<Recipe> recipes, List<Promotion> promotions) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.recipes = recipes;
        this.promotions = promotions;
    }

    public ProductResponse() {

    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
    }

    public List<Promotion> getPromotions() {
        return promotions;
    }

    public void setPromotions(List<Promotion> promotions) {
        this.promotions = promotions;
    }
}

