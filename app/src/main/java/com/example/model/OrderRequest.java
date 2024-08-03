package com.example.model;

import java.util.List;

public class OrderRequest {
    private Integer tableId;
    private String orderStatus;
    private List<OrderDetail> orderDetail;

    // Constructor
    public OrderRequest(int tableId, List<OrderDetail> orderDetail) {
        this.tableId = tableId;
        this.orderDetail = orderDetail;
    }

    public OrderRequest(Integer tableId, String orderStatus, List<OrderDetail> orderDetail) {
        this.tableId = tableId;
        this.orderStatus = orderStatus;
        this.orderDetail = orderDetail;
    }

    // Getters and Setters
    public Integer getTableId() {
        return tableId;
    }

    public void setTableId(Integer tableId) {
        this.tableId = tableId;
    }

    public List<OrderDetail> getOrderDetail() {
        return orderDetail;
    }

    public void setOrderDetail(List<OrderDetail> orderDetail) {
        this.orderDetail = orderDetail;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}