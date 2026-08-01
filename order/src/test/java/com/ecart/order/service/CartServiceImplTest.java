package com.ecart.order.service;

import com.ecart.order.clients.ProductServiceClient;
import com.ecart.order.clients.UserServiceClient;
import com.ecart.order.dto.CartItemRequest;
import com.ecart.order.dto.ProductResponse;
import com.ecart.order.dto.UserResponse;
import com.ecart.order.entity.CartItem;
import com.ecart.order.exception.InsufficientStockException;
import com.ecart.order.repository.CartItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private ProductServiceClient productServiceClient;
    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private CartServiceImpl cartService;

    @Test
    void addToCartRejectsQuantityAboveAvailableStockIncludingExistingCartQuantity() {
        ProductResponse product = new ProductResponse();
        product.setStockQuantity(5);
        product.setPrice(100.0);

        UserResponse user = new UserResponse();
        user.setId(1L);

        CartItem existingCartItem = new CartItem();
        existingCartItem.setQuantity(4);

        CartItemRequest request = new CartItemRequest();
        request.setProductId("10");
        request.setQuantity(2);

        when(productServiceClient.getProductById("10")).thenReturn(product);
        when(userServiceClient.getUserById("1")).thenReturn(user);
        when(cartItemRepository.findByUserIdAndProductId(1L, 10L)).thenReturn(existingCartItem);

        assertThrows(InsufficientStockException.class, () -> cartService.addToCart("1", request));

        verify(cartItemRepository, never()).save(any(CartItem.class));
    }
}
