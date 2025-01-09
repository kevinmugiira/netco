package com.netco.microservices.inventory_service.dto;


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

    public Boolean getIsInStock() {
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

    // Optional: Equals and HashCode methods (if needed for collections)
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        InventoryResponse that = (InventoryResponse) o;
//
//        if (skuCode != null ? !skuCode.equals(that.skuCode) : that.skuCode != null) return false;
//        return isInStock != null ? isInStock.equals(that.isInStock) : that.isInStock == null;
//    }
//
//    @Override
//    public int hashCode() {
//        int result = skuCode != null ? skuCode.hashCode() : 0;
//        result = 31 * result + (isInStock != null ? isInStock.hashCode() : 0);
//        return result;
//    }
}
