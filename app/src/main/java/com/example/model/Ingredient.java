package com.example.model;

public class Ingredient {
    private int id;
    private String name;
    private String price;
    private String unit;
    private double quantity;
    private double warningLimits;

    public Ingredient(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getWarningLimits() {
        return warningLimits;
    }

    public void setWarningLimits(double warningLimits) {
        this.warningLimits = warningLimits;
    }
}
