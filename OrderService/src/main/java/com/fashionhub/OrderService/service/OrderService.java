package com.fashionhub.OrderService.service;

import com.fashionhub.OrderService.dto.InventoryResponse;
import com.fashionhub.OrderService.dto.OrderLineItemDto;
import com.fashionhub.OrderService.dto.OrderRequest;
import com.fashionhub.OrderService.model.Order;
import com.fashionhub.OrderService.model.OrderLineItem;
import com.fashionhub.OrderService.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;

    private final WebClient.Builder webClientBuilder;


    public String placeOrder(OrderRequest orderRequest){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItem> orderLineItems = orderRequest.getOrderLineItems()
                .stream()
                .map(this::mapToOrderLineItem).toList();

        order.setOrderLineItems(orderLineItems);

        List<String> skuCodes = orderLineItems.stream()
                .map(OrderLineItem::getSkuCode).toList();

        // To Check products available in stock or not
        InventoryResponse[] inventoryResponses = webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory", uriBuilder -> uriBuilder.queryParam("skuCodes", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        log.info("Inventory Responses " + inventoryResponses.length);
        boolean allProductsAvailable = Arrays.stream(inventoryResponses).allMatch(InventoryResponse::isInStock);

       if(inventoryResponses.length!=0 && allProductsAvailable) {
           orderRepository.save(order);
           return  "Order Placed Succesfully";
       }
       else{
           throw new RuntimeException("Product not available in stock");
       }
    }

    private OrderLineItem mapToOrderLineItem(OrderLineItemDto orderLineItemDto){
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setSkuCode(orderLineItemDto.getSkuCode());
        orderLineItem.setQuantity(orderLineItemDto.getQuantity());
        orderLineItem.setPrice(orderLineItemDto.getPrice());
        return orderLineItem;
    }
}
