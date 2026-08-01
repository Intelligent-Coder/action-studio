package org.example.service;

import org.example.client.OrderServiceClient;
import org.example.dto.PaymentRequestDto;
import org.example.dto.PaymentResponseDto;
import org.example.entity.Payment;
import org.example.exception.InvalidPaymentException;
import org.example.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private TransactionService transactionService;

    @Mock
    private OrderServiceClient orderServiceClient;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void createPaymentSuccessfully() {
        PaymentRequestDto request = new PaymentRequestDto();
        request.setOrderId(100L);
        request.setUserId(1L);
        request.setAmount(BigDecimal.valueOf(1598.00));
        request.setCurrency("USD");
        request.setPaymentMethod("CREDIT_CARD");
        request.setDescription("Test Payment");

        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment p = invocation.getArgument(0);
            p.setId(1L);
            return p;
        });

        PaymentResponseDto response = paymentService.createPayment(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(100L, response.getOrderId());
        assertEquals(1L, response.getUserId());
        assertEquals("PENDING", response.getStatus());
        verify(paymentRepository).save(any(Payment.class));
        verify(transactionService).logTransaction(any(Payment.class), eq("AUTHORIZE"), anyString());
    }

    @Test
    void createPaymentFailsWhenAmountIsZeroOrNegative() {
        PaymentRequestDto request = new PaymentRequestDto();
        request.setAmount(BigDecimal.ZERO);

        assertThrows(InvalidPaymentException.class, () -> paymentService.createPayment(request));
        verifyNoInteractions(paymentRepository);
    }

    @Test
    void completePaymentNotifiesOrderService() {
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setOrderId(100L);
        payment.setStatus(Payment.PaymentStatus.PENDING);

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArgument(0));

        PaymentResponseDto response = paymentService.completePayment(1L);

        assertEquals("COMPLETED", response.getStatus());
        verify(orderServiceClient).updateOrderStatus(100L, "CONFIRMED");
    }

    @Test
    void failPaymentNotifiesOrderServiceCancellation() {
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setOrderId(100L);
        payment.setStatus(Payment.PaymentStatus.PENDING);

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArgument(0));

        PaymentResponseDto response = paymentService.failPayment(1L, "Insufficient funds");

        assertEquals("FAILED", response.getStatus());
        verify(orderServiceClient).updateOrderStatus(100L, "CANCELLED");
    }
}
