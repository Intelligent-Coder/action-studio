package com.ecart.order.service;

import com.ecart.order.dto.CartItemRequest;
import com.ecart.order.dto.CartItemResponse;
import com.ecart.order.entity.CartItem;

import java.util.List;

public interface CartService {
    void addToCart(String userId, CartItemRequest cartRequest);

    boolean removeFromCart(String userId, String productId);

    List<CartItemResponse> getCartItems(String userId);
    List<CartItem> getCartItemsDB(String userId);

    void clearCart(String userId);
}
