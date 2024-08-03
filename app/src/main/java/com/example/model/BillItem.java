package com.example.model;

public class BillItem {
    private String productName;
    private String quantity;
    private String unitPrice;
    private String totalPrice;

    public BillItem(String productName, String quantity, String unitPrice, String totalPrice) {
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
    }

    // Getters and setters
    public String getProductName() {
        return productName;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public String getTotalPrice() {
        return totalPrice;
    }
}
