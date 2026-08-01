package com.ecart.order.service;

import com.ecart.order.dto.OrderResponse;

public interface OrderService {
    OrderResponse createOrder(String userId);
}
