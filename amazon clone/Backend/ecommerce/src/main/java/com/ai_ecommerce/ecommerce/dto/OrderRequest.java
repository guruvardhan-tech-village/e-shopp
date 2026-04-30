package com.ai_ecommerce.ecommerce.dto;

import java.util.List;

public class OrderRequest {
    private String email;
    private String address;
    private List<OrderItemRequest> items;

    public OrderRequest() {}

    public OrderRequest(String email, String address, List<OrderItemRequest> items) {
        this.email = email;
        this.address = address;
        this.items = items;
    }

    //getters
    public String getEmail() {
        return email;
    }
    public String getAddress() {
        return address;
    }
    public List<OrderItemRequest> getItems() {
        return items;
    }
    //setters

    public void setEmail(String email) {
        this.email = email;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }
}