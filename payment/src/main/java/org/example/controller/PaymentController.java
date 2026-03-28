package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.dto.PaymentRequestDto;
import org.example.dto.PaymentResponseDto;
import org.example.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@Tag(name = "Payment Management", description = "APIs for managing payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    @Operation(summary = "Create a new payment", description = "Initiate a new payment transaction")
    public ResponseEntity<PaymentResponseDto> createPayment(@Valid @RequestBody PaymentRequestDto paymentRequestDto) {
        PaymentResponseDto createdPayment = paymentService.createPayment(paymentRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPayment);
    }

    @PostMapping("/{paymentId}/process")
    @Operation(summary = "Process payment", description = "Move payment from PENDING to PROCESSING status")
    public ResponseEntity<PaymentResponseDto> processPayment(@PathVariable Long paymentId) {
        PaymentResponseDto payment = paymentService.processPayment(paymentId);
        return ResponseEntity.ok(payment);
    }

    @PostMapping("/{paymentId}/complete")
    @Operation(summary = "Complete payment", description = "Mark payment as COMPLETED")
    public ResponseEntity<PaymentResponseDto> completePayment(@PathVariable Long paymentId) {
        PaymentResponseDto payment = paymentService.completePayment(paymentId);
        return ResponseEntity.ok(payment);
    }

    @PostMapping("/{paymentId}/refund")
    @Operation(summary = "Refund payment", description = "Refund a completed payment")
    public ResponseEntity<PaymentResponseDto> refundPayment(@PathVariable Long paymentId) {
        PaymentResponseDto payment = paymentService.refundPayment(paymentId);
        return ResponseEntity.ok(payment);
    }

    @PostMapping("/{paymentId}/fail")
    @Operation(summary = "Mark payment as failed", description = "Mark a payment as failed with error message")
    public ResponseEntity<PaymentResponseDto> failPayment(@PathVariable Long paymentId,
                                                           @RequestParam String errorMessage) {
        PaymentResponseDto payment = paymentService.failPayment(paymentId, errorMessage);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/{paymentId}")
    @Operation(summary = "Get payment by ID", description = "Retrieve a payment by its ID")
    public ResponseEntity<PaymentResponseDto> getPayment(@PathVariable Long paymentId) {
        PaymentResponseDto payment = paymentService.getPayment(paymentId);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/transaction/{transactionId}")
    @Operation(summary = "Get payment by transaction ID", description = "Retrieve a payment by its transaction ID")
    public ResponseEntity<PaymentResponseDto> getPaymentByTransactionId(@PathVariable String transactionId) {
        PaymentResponseDto payment = paymentService.getPaymentByTransactionId(transactionId);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get payments by user ID", description = "Retrieve all payments for a specific user")
    public ResponseEntity<List<PaymentResponseDto>> getPaymentsByUserId(@PathVariable Long userId) {
        List<PaymentResponseDto> payments = paymentService.getPaymentsByUserId(userId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get payments by status", description = "Retrieve all payments with a specific status")
    public ResponseEntity<List<PaymentResponseDto>> getPaymentsByStatus(@PathVariable String status) {
        List<PaymentResponseDto> payments = paymentService.getPaymentsByStatus(status);
        return ResponseEntity.ok(payments);
    }

    @GetMapping
    @Operation(summary = "Get all payments", description = "Retrieve a list of all payments")
    public ResponseEntity<List<PaymentResponseDto>> getAllPayments() {
        List<PaymentResponseDto> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }
}
