package com.ecart.order.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemResponse {
    private Long userId;
    private Long productId;
    private Integer quantity;
    private BigDecimal price;
}
