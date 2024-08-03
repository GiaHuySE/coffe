package com.example.model;

import java.util.List;

public class Recipe {
    private Ingredient ingredient;
    private double quantity;

    // Getters and setters
    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }
}
