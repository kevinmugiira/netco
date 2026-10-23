package com.netco.microservices.order_service.service;

import com.netco.microservices.order_service.dto.OrderRequest;
import java.math.BigDecimal;
import java.util.List;

public record OrderPlacedEvent(String orderNumber, String customerEmail,
                               OrderRequest.ShippingAddress shippingAddress, List<OrderItem> items) {
    public record OrderItem(String skuCode, BigDecimal price, Integer quantity) {}
}
