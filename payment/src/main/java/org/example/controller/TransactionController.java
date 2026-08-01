package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.dto.TransactionDto;
import org.example.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Transaction Management", description = "APIs for managing transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @GetMapping("/{transactionId}")
    @Operation(summary = "Get transaction by ID", description = "Retrieve a transaction by its ID")
    @Parameter(name = "transactionId", description = "ID of the transaction", example = "123")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transaction retrieved successfully",
                     content = @Content(schema = @Schema(implementation = TransactionDto.class))),
        @ApiResponse(responseCode = "404", description = "Transaction not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<TransactionDto> getTransaction(@PathVariable Long transactionId) {
        TransactionDto transaction = transactionService.getTransaction(transactionId);
        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/payment/{paymentId}")
    @Operation(summary = "Get transactions by payment ID", description = "Retrieve all transactions for a specific payment")
    @Parameter(name = "paymentId", description = "ID of the payment", example = "456")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully",
                     content = @Content(schema = @Schema(implementation = TransactionDto.class))),
        @ApiResponse(responseCode = "404", description = "Payment not found or no transactions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<TransactionDto>> getTransactionsByPaymentId(@PathVariable Long paymentId) {
        List<TransactionDto> transactions = transactionService.getTransactionsByPaymentId(paymentId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping
    @Operation(summary = "Get all transactions", description = "Retrieve a list of all transactions")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully",
                     content = @Content(schema = @Schema(implementation = TransactionDto.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<TransactionDto>> getAllTransactions() {
        List<TransactionDto> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }

    @DeleteMapping("/{transactionId}")
    @Operation(summary = "Delete transaction by ID", description = "Delete a transaction by its ID")
    @Parameter(name = "transactionId", description = "ID of the transaction to delete", example = "123")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Transaction deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Transaction not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long transactionId) {
        transactionService.deleteTransaction(transactionId);
        return ResponseEntity.noContent().build();
    }




}

