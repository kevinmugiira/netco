package com.netco.microservices.notification_service.event;

import java.math.BigDecimal;
import java.util.List;

public record OrderPlacedEvent(String orderNumber, String customerEmail, ShippingAddress shippingAddress, List<OrderItem> items) {
    public record ShippingAddress(String name, String street1, String city, String state, String zip, String country, String phone) {}
    public record OrderItem(String skuCode, BigDecimal price, Integer quantity) {}
}
