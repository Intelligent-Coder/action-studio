package org.example.service;

import org.example.dto.TransactionDto;
import org.example.entity.Payment;
import org.example.entity.Transaction;
import org.example.exception.ResourceNotFoundException;
import org.example.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TransactionService {
    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public TransactionDto logTransaction(Payment payment, String transactionType, String description) {
        Transaction transaction = new Transaction();
        transaction.setPayment(payment);
        transaction.setTransactionType(Transaction.TransactionType.valueOf(transactionType));
        transaction.setDescription(description);

        Transaction savedTransaction = transactionRepository.save(transaction);
        return convertToDto(savedTransaction);
    }

    public TransactionDto logTransaction(Payment payment, String transactionType, String description, String gatewayResponse) {
        Transaction transaction = new Transaction();
        transaction.setPayment(payment);
        transaction.setTransactionType(Transaction.TransactionType.valueOf(transactionType));
        transaction.setDescription(description);
        transaction.setGatewayResponse(gatewayResponse);

        Transaction savedTransaction = transactionRepository.save(transaction);
        return convertToDto(savedTransaction);
    }

    public TransactionDto logFailedTransaction(Payment payment, String transactionType, String errorMessage) {
        Transaction transaction = new Transaction();
        transaction.setPayment(payment);
        transaction.setTransactionType(Transaction.TransactionType.valueOf(transactionType));
        transaction.setErrorMessage(errorMessage);

        Transaction savedTransaction = transactionRepository.save(transaction);
        return convertToDto(savedTransaction);
    }

    public TransactionDto getTransaction(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + transactionId));
        return convertToDto(transaction);
    }

    public List<TransactionDto> getTransactionsByPaymentId(Long paymentId) {
        return transactionRepository.findByPaymentId(paymentId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<TransactionDto> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private TransactionDto convertToDto(Transaction transaction) {
        TransactionDto dto = new TransactionDto();
        dto.setId(transaction.getId());
        dto.setPaymentId(transaction.getPayment().getId());
        dto.setTransactionType(transaction.getTransactionType().toString());
        dto.setDescription(transaction.getDescription());
        dto.setGatewayResponse(transaction.getGatewayResponse());
        dto.setErrorMessage(transaction.getErrorMessage());
        dto.setCreatedAt(transaction.getCreatedAt());
        return dto;
    }
}
