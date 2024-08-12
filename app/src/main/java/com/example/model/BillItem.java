package com.example.model;

public class BillItem {
    private final String productName;
    private final String quantity;
    private final String unitPrice;
    private final String totalPrice;
    private  final String discount;

    public BillItem(String productName, String quantity, String unitPrice, String totalPrice, String discount) {
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
        this.discount = discount;
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

    public String getDiscount() {
        return discount;
    }
}
