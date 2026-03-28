package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.dto.TransactionDto;
import org.example.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Transaction Management", description = "APIs for managing transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/{transactionId}")
    @Operation(summary = "Get transaction by ID", description = "Retrieve a transaction by its ID")
    public ResponseEntity<TransactionDto> getTransaction(@PathVariable Long transactionId) {
        TransactionDto transaction = transactionService.getTransaction(transactionId);
        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/payment/{paymentId}")
    @Operation(summary = "Get transactions by payment ID", description = "Retrieve all transactions for a specific payment")
    public ResponseEntity<List<TransactionDto>> getTransactionsByPaymentId(@PathVariable Long paymentId) {
        List<TransactionDto> transactions = transactionService.getTransactionsByPaymentId(paymentId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping
    @Operation(summary = "Get all transactions", description = "Retrieve a list of all transactions")
    public ResponseEntity<List<TransactionDto>> getAllTransactions() {
        List<TransactionDto> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }
}
