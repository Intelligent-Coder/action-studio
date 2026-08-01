package com.ecart.order.service;

import com.ecart.order.dto.OrderResponse;
import com.ecart.order.entity.OrderStatus;

public interface OrderService {
    OrderResponse createOrder(String userId);
    OrderResponse getOrderById(Long id);
    OrderResponse updateOrderStatus(Long id, OrderStatus status);
}
