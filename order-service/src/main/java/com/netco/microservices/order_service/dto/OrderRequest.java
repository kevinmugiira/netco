package com.netco.microservices.order_service.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

//@Data
//@AllArgsConstructor
//@NoArgsConstructor
public class OrderRequest {
    private List<OrderLineItemsDto> orderLineItemsDtoList;
    private String customerEmail;
    private ShippingAddress shippingAddress;

    public static class ShippingAddress {
        private String name;
        private String street1;
        private String city;
        private String state;
        private String zip;
        private String country;
        private String phone;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getStreet1() { return street1; }
        public void setStreet1(String street1) { this.street1 = street1; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
        public String getZip() { return zip; }
        public void setZip(String zip) { this.zip = zip; }
        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
    }

    public OrderRequest() {

    }

    // All-args constructor
    public OrderRequest(List<OrderLineItemsDto> orderLineItemsDtoList) {
        this.orderLineItemsDtoList = orderLineItemsDtoList;
    }

    // Getter and setter for orderLineItemsDtoList
    public List<OrderLineItemsDto> getOrderLineItemsDtoList() {
        return orderLineItemsDtoList;
    }

    public void setOrderLineItemsDtoList(List<OrderLineItemsDto> orderLineItemsDtoList) {
        this.orderLineItemsDtoList = orderLineItemsDtoList;
    }
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    public ShippingAddress getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(ShippingAddress shippingAddress) { this.shippingAddress = shippingAddress; }

    // Override toString() for easy debugging
    @Override
    public String toString() {
        return "OrderRequest{" +
                "orderLineItemsDtoList=" + orderLineItemsDtoList +
                '}';
    }
}
