package com.ai_ecommerce.ecommerce.dto;

public class OrderItemRequest {
    private Long productId;
    private int quantity;

    public OrderItemRequest() {}
    public OrderItemRequest(Long productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    // getters setters
    public Long getProductId() {
        return productId;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}