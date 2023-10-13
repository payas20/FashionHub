package com.fashionhub.OrderService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderLineItemDto {
    private Long Id;
    private String skuCode;
    private Integer quantity;
    private BigDecimal price;
}
