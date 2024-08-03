package com.example.model;

public class OrderDetailDto {
    private int id;
    private ProductResponse productDto;
    private int totalPrice;
    private int quantity;
    private int discount;
    private int cost;

    public OrderDetailDto() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ProductResponse getProductDto() {
        return productDto;
    }

    public void setProductDto(ProductResponse productDto) {
        this.productDto = productDto;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public OrderDetailDto(int id, ProductResponse productDto, int totalPrice, int quantity, int discount, int cost) {
        this.id = id;
        this.productDto = productDto;
        this.totalPrice = totalPrice;
        this.quantity = quantity;
        this.discount = discount;
        this.cost = cost;
    }
}