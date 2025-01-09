package com.netco.microservices.inventory_service.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "t_inventory")
//@Getter
//@Setter
//@AllArgsConstructor
//@NoArgsConstructor
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String skuCode;
    private Integer quantity;

    public Inventory() {
    }

    public Inventory(Long id, String skuCode, Integer quantity) {
        this.id = id;
        this.skuCode = skuCode;
        this.quantity = quantity;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

//    public String getSkuCode() {
//        return skuCode;
//    }

//    public void setSkuCode(String skuCode) {
//        this.skuCode = skuCode;
//    }


    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getSkuCode() {return skuCode;}

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getQuantity() { return quantity; }

    @Override
    public String toString() {
        return "Inventory{" +
                "id=" + id +
                ", skuCode=" + skuCode +
                ", quantity=" + quantity +
                '}' ;
    }
}
