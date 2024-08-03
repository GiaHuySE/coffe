package com.example.model;

import java.util.List;

public class Order {
    private String name;
    private String zone;
    private String timeOrder;
    private String nvOrder;

    private List<Ingredient> ingredients;

    public Order(String name, String zone, String timeOrder, String nvOrder) {
        this.name = name;
        this.zone = zone;
        this.timeOrder = timeOrder;
        this.nvOrder = nvOrder;
    }

    public Order(String name, String zone, String timeOrder, String nvOrder, List<Ingredient> ingredients) {
        this.name = name;
        this.zone = zone;
        this.timeOrder = timeOrder;
        this.nvOrder = nvOrder;
        this.ingredients = ingredients;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getTimeOrder() {
        return timeOrder;
    }

    public void setTimeOrder(String timeOrder) {
        this.timeOrder = timeOrder;
    }

    public String getNvOrder() {
        return nvOrder;
    }

    public void setNvOrder(String nvOrder) {
        this.nvOrder = nvOrder;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }
}