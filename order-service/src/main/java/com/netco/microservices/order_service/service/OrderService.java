package com.netco.microservices.order_service.service;

import com.netco.microservices.order_service.dto.InventoryResponse;
import com.netco.microservices.order_service.dto.OrderLineItemsDto;
import com.netco.microservices.order_service.dto.OrderRequest;
import com.netco.microservices.order_service.model.Order;
import com.netco.microservices.order_service.model.OrderLineItems;
import com.netco.microservices.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
//@Slf4j
//@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
//    private final WebClient webClient;

    public OrderService(OrderRepository orderRepository, WebClient.Builder webClientBuilder) {
        this.orderRepository = orderRepository;
        this.webClientBuilder = webClientBuilder;
//        this.webClient = webClient;
    }

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
        System.out.println("Items: " + orderLineItems);

        List<String> skuCodes = order.getOrderLineItemsList()
                .stream()
                .map(OrderLineItems::getSkuCode)
                .toList();

        System.out.println("SKU Codes: " + skuCodes);
//        String skuCodesString = String.join(",", skuCodes);

        /*
         * call the inventory service and ensure
         * that the product is in stock
         */
        InventoryResponse[] inventoryResponseArray = webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block(); //block identifies the call as synchronous


        boolean allProductsIsInStock = Arrays
                .stream(inventoryResponseArray)
                .allMatch(InventoryResponse::getIsInStock); //inventoryResponse -> inventoryResponse.isInStock()

        if (allProductsIsInStock) {
            orderRepository.save(order);
        } else {
            throw new IllegalArgumentException("Product is not in stock, please try again later!");
        }

    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }
}
