package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
    private Long id;
    private Long paymentId;
    private String transactionType;
    private String description;
    private String gatewayResponse;
    private String errorMessage;
    private LocalDateTime createdAt;
}
