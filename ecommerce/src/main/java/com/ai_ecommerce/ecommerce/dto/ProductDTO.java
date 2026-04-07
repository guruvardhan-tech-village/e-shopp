package com.ai_ecommerce.ecommerce.dto;

import jakarta.validation.constraints.*;

public class ProductDTO {

    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank(message = "Description is required")
    private String description;
    @Min(value = 1, message = "Price must be greater than Zero")
    private Double price;
    @NotBlank(message = "Company Name is required")
    private String companyName;
    @NotBlank(message = "Category is required")
    private String category;
    @NotBlank(message = "Status is required")
    private String status;
    @Min(value = 0, message = "Quantity must have value greater than or equal to zero")
    private int quantity;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Double getPrice() {
        return price;
    }
    public void setPrice(Double price) {
        this.price = price;
    }
    public String getCompanyName() {
        return companyName;
    }
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
