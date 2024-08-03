package com.example.model;

import java.util.List;

public class OrderResponseDto {
    private int id;
    private String timeIn;
    private String timeOut;
    private User userIn;
    private User userOut;
    private String status;
    private Table table;
    private List<OrderDetailDto> orderDetails;

    public OrderResponseDto() {

    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTimeIn() {
        return timeIn;
    }

    public void setTimeIn(String timeIn) {
        this.timeIn = timeIn;
    }

    public String getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(String timeOut) {
        this.timeOut = timeOut;
    }

    public User getUserIn() {
        return userIn;
    }

    public void setUserIn(User userIn) {
        this.userIn = userIn;
    }

    public User getUserOut() {
        return userOut;
    }

    public void setUserOut(User userOut) {
        this.userOut = userOut;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public List<OrderDetailDto> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetailDto> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public OrderResponseDto(int id, String timeIn, String timeOut, User userIn, User userOut, String status, Table table, List<OrderDetailDto> orderDetails) {
        this.id = id;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        this.userIn = userIn;
        this.userOut = userOut;
        this.status = status;
        this.table = table;
        this.orderDetails = orderDetails;
    }
}