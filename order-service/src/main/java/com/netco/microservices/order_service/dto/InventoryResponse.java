package com.netco.microservices.order_service.dto;

public class InventoryResponse {

    private String skuCode;
    private boolean isInStock;

    // No-args constructor
    public InventoryResponse() {
    }

    // All-args constructor
    public InventoryResponse(String skuCode, Boolean isInStock) {
        this.skuCode = skuCode;
        this.isInStock = isInStock;
    }

    // Getters and Setters
    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public boolean getIsInStock() {
        return isInStock;
    }

    public void setIsInStock(Boolean isInStock) {
        this.isInStock = isInStock;
    }

    // Override toString method if needed
    @Override
    public String toString() {
        return "InventoryResponse{" +
                "skuCode='" + skuCode + '\'' +
                ", isInStock=" + isInStock +
                '}';
    }

}
