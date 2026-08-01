package com.ecart.order.service;

import com.ecart.order.clients.ProductServiceClient;
import com.ecart.order.dto.OrderResponse;
import com.ecart.order.dto.StockUpdateRequest;
import com.ecart.order.entity.CartItem;
import com.ecart.order.entity.Order;
import com.ecart.order.exception.EmptyCartException;
import com.ecart.order.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private CartService cartService;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductServiceClient productServiceClient;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void createOrderRejectsAnEmptyCart() {
        when(cartService.getCartItemsDB("1")).thenReturn(List.of());

        assertThrows(EmptyCartException.class, () -> orderService.createOrder("1"));

        verifyNoInteractions(productServiceClient, orderRepository);
    }

    @Test
    void createOrderDecrementsStockBeforeSavingTheOrder() {
        CartItem cartItem = new CartItem();
        cartItem.setProductId(10L);
        cartItem.setQuantity(2);
        cartItem.setPrice(BigDecimal.valueOf(50));

        when(cartService.getCartItemsDB("1")).thenReturn(List.of(cartItem));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            return order;
        });

        OrderResponse response = orderService.createOrder("1");

        ArgumentCaptor<StockUpdateRequest> requestCaptor = ArgumentCaptor.forClass(StockUpdateRequest.class);
        verify(productServiceClient).decrementStock(org.mockito.ArgumentMatchers.eq(10L), requestCaptor.capture());
        verify(orderRepository).save(any(Order.class));
        verify(cartService).clearCart("1");
        assertEquals(2, requestCaptor.getValue().getQuantity());
        assertEquals(BigDecimal.valueOf(100), response.getTotalAmount());
    }
}
