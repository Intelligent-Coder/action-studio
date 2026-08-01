package com.ecart.order.service;

import com.ecart.order.clients.ProductServiceClient;
import com.ecart.order.clients.UserServiceClient;
import com.ecart.order.dto.CartItemRequest;
import com.ecart.order.dto.CartItemResponse;
import com.ecart.order.dto.ProductResponse;
import com.ecart.order.dto.UserResponse;
import com.ecart.order.entity.CartItem;
import com.ecart.order.exception.InsufficientStockException;
import com.ecart.order.exception.ResourceNotFoundException;
import com.ecart.order.exception.ServiceUnavailableException;
import com.ecart.order.repository.CartItemRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {
    private final CartItemRepository cartItemRepository;
    private final ProductServiceClient productServiceClient;
    private final UserServiceClient userServiceClient;

    @Override
    @CircuitBreaker(name = "productService", fallbackMethod = "addToCartFallBack")
    public void addToCart(String userId, CartItemRequest cartRequest) {
        ProductResponse productResponse = productServiceClient.getProductById(cartRequest.getProductId());
        if (productResponse == null) {
            throw new ResourceNotFoundException("Product not found");
        }
        UserResponse userResponse = userServiceClient.getUserById(userId);
        if (userResponse == null) {
            throw new ResourceNotFoundException("User not found");
        }
        Long userIdLong = Long.valueOf(userId);
        Long productIdLong = Long.valueOf(cartRequest.getProductId());
        CartItem existingCartItem = cartItemRepository.findByUserIdAndProductId(userIdLong, productIdLong);
        int existingQuantity = existingCartItem == null ? 0 : existingCartItem.getQuantity();
        if (productResponse.getStockQuantity() == null
                || existingQuantity + cartRequest.getQuantity() > productResponse.getStockQuantity()) {
            throw new InsufficientStockException("Insufficient stock");
        }

        if (existingCartItem != null) {
            existingCartItem.setQuantity(existingCartItem.getQuantity() + cartRequest.getQuantity());
            existingCartItem.setPrice(BigDecimal.valueOf(productResponse.getPrice()));
            cartItemRepository.save(existingCartItem);
        } else {
            CartItem newCartItem = new CartItem();
            newCartItem.setUserId(userIdLong);
            newCartItem.setProductId(productIdLong);
            newCartItem.setQuantity(cartRequest.getQuantity());
            newCartItem.setPrice(BigDecimal.valueOf(productResponse.getPrice()));
            cartItemRepository.save(newCartItem);
        }
    }

    @Override
    @Transactional
    public boolean removeFromCart(String userId, String productId) {
        Long userIdLong = Long.valueOf(userId);
        Long productIdLong = Long.valueOf(productId);
        CartItem cartItem = cartItemRepository.findByUserIdAndProductId(userIdLong, productIdLong);
        if (cartItem == null) {
            return false;
        }
        cartItemRepository.delete(cartItem);
        return true;
    }

    @Override
    public List<CartItemResponse> getCartItems(String userId) {
        return getCartItemsDB(userId).stream().map(this::mapToResponse)
                .filter(Objects::nonNull).toList();
    }

    @Override
    public List<CartItem> getCartItemsDB(String userId) {
        return cartItemRepository.findByUserId(Long.valueOf(userId)).stream().toList();
    }

    @Override
    public void clearCart(String userId) {
        Long userIdLong = Long.valueOf(userId);
        List<CartItem> cartItems = cartItemRepository.findByUserId(userIdLong);
        if (cartItems != null && !cartItems.isEmpty()) {
            cartItemRepository.deleteAll(cartItems);
        }
    }


    private CartItemResponse mapToResponse(CartItem cartItem) {
        ProductResponse product = productServiceClient.getProductById(String.valueOf(cartItem.getProductId()));
        if (product == null) {
            return null;
        }
        CartItemResponse cartItemResponse = new CartItemResponse();
        cartItemResponse.setProductId(product.getId());
        cartItemResponse.setPrice(BigDecimal.valueOf(product.getPrice()));
        cartItemResponse.setQuantity(cartItem.getQuantity());
        return cartItemResponse;

    }

    public void addToCartFallBack(String userId, CartItemRequest cartRequest, Exception exception) {
        if (exception instanceof RestClientResponseException remoteException
                && remoteException.getStatusCode().is4xxClientError()) {
            throw remoteException;
        }
        if (exception instanceof InsufficientStockException || exception instanceof ResourceNotFoundException) {
            throw (RuntimeException) exception;
        }
        throw new ServiceUnavailableException("User or product service is unavailable");
    }
}
