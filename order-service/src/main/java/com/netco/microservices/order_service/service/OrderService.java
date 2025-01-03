package com.netco.microservices.order_service.service;

import com.netco.microservices.order_service.dto.OrderLineItemsDto;
import com.netco.microservices.order_service.dto.OrderRequest;
import com.netco.microservices.order_service.model.Order;
import com.netco.microservices.order_service.model.OrderLineItems;
import com.netco.microservices.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public void placeOrder(OrderRequest orderRequest) {
        if (orderRequest.getOrderLineItemsDtoList() == null) {
            throw new IllegalArgumentException("OrderLineItemsDtoList is null");
        } else if (orderRequest == null) {
            throw new IllegalArgumentException("orderRequest is null");
        }


        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();
        order.setOrderLineItemsList(orderLineItems);
        orderRepository.save(order);
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItems.getQuantity());
        orderLineItems.setSkuCode(orderLineItems.getSkuCode());
        return orderLineItems;
    }
}
