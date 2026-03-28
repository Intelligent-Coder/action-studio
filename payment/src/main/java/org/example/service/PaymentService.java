package org.example.service;

import org.example.dto.PaymentRequestDto;
import org.example.dto.PaymentResponseDto;
import org.example.entity.Payment;
import org.example.entity.User;
import org.example.exception.InvalidPaymentException;
import org.example.exception.ResourceNotFoundException;
import org.example.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final TransactionService transactionService;

    public PaymentService(PaymentRepository paymentRepository,
                         UserRepository userRepository,
                         TransactionService transactionService) {
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.transactionService = transactionService;
    }

    public PaymentResponseDto createPayment(PaymentRequestDto paymentRequestDto) {
        User user = userRepository.findById(paymentRequestDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + paymentRequestDto.getUserId()));

        if (paymentRequestDto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidPaymentException("Payment amount must be greater than zero");
        }

        Payment payment = new Payment();
        payment.setTransactionId(UUID.randomUUID().toString());
        payment.setUser(user);
        payment.setAmount(paymentRequestDto.getAmount());
        payment.setCurrency(paymentRequestDto.getCurrency());
        payment.setPaymentMethod(Payment.PaymentMethod.valueOf(paymentRequestDto.getPaymentMethod()));
        payment.setDescription(paymentRequestDto.getDescription());
        payment.setReferenceNumber(paymentRequestDto.getReferenceNumber());
        payment.setStatus(Payment.PaymentStatus.PENDING);

        Payment savedPayment = paymentRepository.save(payment);

        // Log transaction
        transactionService.logTransaction(savedPayment, "AUTHORIZE", "Payment authorization initiated");

        return convertToDto(savedPayment);
    }

    public PaymentResponseDto processPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));

        if (payment.getStatus() != Payment.PaymentStatus.PENDING) {
            throw new InvalidPaymentException("Payment cannot be processed. Current status: " + payment.getStatus());
        }

        payment.setStatus(Payment.PaymentStatus.PROCESSING);
        Payment savedPayment = paymentRepository.save(payment);

        transactionService.logTransaction(savedPayment, "CAPTURE", "Payment processing started");

        return convertToDto(savedPayment);
    }

    public PaymentResponseDto completePayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));

        if (payment.getStatus() != Payment.PaymentStatus.PROCESSING) {
            throw new InvalidPaymentException("Payment cannot be completed. Current status: " + payment.getStatus());
        }

        payment.setStatus(Payment.PaymentStatus.COMPLETED);
        payment.setProcessedAt(LocalDateTime.now());
        Payment savedPayment = paymentRepository.save(payment);

        transactionService.logTransaction(savedPayment, "VERIFICATION", "Payment completed successfully");

        return convertToDto(savedPayment);
    }

    public PaymentResponseDto refundPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));

        if (payment.getStatus() != Payment.PaymentStatus.COMPLETED) {
            throw new InvalidPaymentException("Only completed payments can be refunded");
        }

        payment.setStatus(Payment.PaymentStatus.REFUNDED);
        Payment savedPayment = paymentRepository.save(payment);

        transactionService.logTransaction(savedPayment, "REFUND", "Payment refunded");

        return convertToDto(savedPayment);
    }

    public PaymentResponseDto failPayment(Long paymentId, String errorMessage) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));

        payment.setStatus(Payment.PaymentStatus.FAILED);
        Payment savedPayment = paymentRepository.save(payment);

        transactionService.logFailedTransaction(savedPayment, "VOID", errorMessage);

        return convertToDto(savedPayment);
    }

    public PaymentResponseDto getPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));
        return convertToDto(payment);
    }

    public PaymentResponseDto getPaymentByTransactionId(String transactionId) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with transaction id: " + transactionId));
        return convertToDto(payment);
    }

    public List<PaymentResponseDto> getPaymentsByUserId(Long userId) {
        return paymentRepository.findByUserId(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<PaymentResponseDto> getPaymentsByStatus(String status) {
        Payment.PaymentStatus paymentStatus = Payment.PaymentStatus.valueOf(status);
        return paymentRepository.findByStatus(paymentStatus).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<PaymentResponseDto> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private PaymentResponseDto convertToDto(Payment payment) {
        PaymentResponseDto dto = new PaymentResponseDto();
        dto.setId(payment.getId());
        dto.setTransactionId(payment.getTransactionId());
        dto.setUserId(payment.getUser().getId());
        dto.setAmount(payment.getAmount());
        dto.setCurrency(payment.getCurrency());
        dto.setStatus(payment.getStatus().toString());
        dto.setPaymentMethod(payment.getPaymentMethod().toString());
        dto.setDescription(payment.getDescription());
        dto.setReferenceNumber(payment.getReferenceNumber());
        dto.setCreatedAt(payment.getCreatedAt());
        dto.setUpdatedAt(payment.getUpdatedAt());
        dto.setProcessedAt(payment.getProcessedAt());
        return dto;
    }
}
