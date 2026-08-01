package com.ecart.order.service;

import com.ecart.order.clients.ProductServiceClient;
import com.ecart.order.dto.OrderItemDTO;
import com.ecart.order.dto.OrderResponse;
import com.ecart.order.dto.StockUpdateRequest;
import com.ecart.order.entity.CartItem;
import com.ecart.order.entity.Order;
import com.ecart.order.entity.OrderItem;
import com.ecart.order.entity.OrderStatus;
import com.ecart.order.exception.EmptyCartException;
import com.ecart.order.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {
    private final CartService cartService;
    private final OrderRepository orderRepository;
    private final ProductServiceClient productServiceClient;

    @Override
    public OrderResponse createOrder(String userId) {
        List<CartItem> cartItems = cartService.getCartItemsDB(userId);
        if (cartItems == null || cartItems.isEmpty()) {
            throw new EmptyCartException("Cannot create an order from an empty cart");
        }

        cartItems.forEach(cartItem -> {
            StockUpdateRequest request = new StockUpdateRequest();
            request.setQuantity(cartItem.getQuantity());
            productServiceClient.decrementStock(cartItem.getProductId(), request);
        });

        var totalAmount = cartItems.stream()
                .map(cartItem -> cartItem.getPrice()
                        .multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Order order = new Order();
        order.setUserId(Long.valueOf(userId));
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.PENDING);
        List<OrderItem> orderItems = cartItems.stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setProductId(cartItem.getProductId());
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setPrice(cartItem.getPrice());
                    orderItem.setOrder(order);
                    return orderItem;
                }).toList();
        order.setItems(orderItems);
        Order savedOrder = orderRepository.save(order);
        cartService.clearCart(userId);
        return mapToOrderResponse(savedOrder);

    }

    private OrderResponse mapToOrderResponse(Order savedOrder) {
        final OrderResponse response = new OrderResponse();
        response.setId(savedOrder.getId());
        response.setStatus(savedOrder.getStatus());
        response.setTotalAmount(savedOrder.getTotalAmount());
        response.setItems(savedOrder.getItems().stream()
                .map(orderItem -> {
                    OrderItemDTO orderItemDTO = new OrderItemDTO();
                    orderItemDTO.setProductId(orderItem.getProductId());
                    orderItemDTO.setQuantity(orderItem.getQuantity());
                    orderItemDTO.setPrice(orderItem.getPrice());
                    return orderItemDTO;
                }).toList());
        return response;
    }
}
