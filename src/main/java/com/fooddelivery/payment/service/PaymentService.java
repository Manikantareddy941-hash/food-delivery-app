package com.fooddelivery.payment.service;

import com.fooddelivery.common.enums.PaymentMethod;
import com.fooddelivery.common.enums.PaymentStatus;
import com.fooddelivery.common.exception.ResourceNotFoundException;
import com.fooddelivery.order.entity.Order;
import com.fooddelivery.order.repository.OrderRepository;
import com.fooddelivery.payment.dto.PaymentRequest;
import com.fooddelivery.payment.dto.PaymentResponse;
import com.fooddelivery.payment.entity.Payment;
import com.fooddelivery.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    
    @Value("${app.payment.mock-gateway.success-rate:0.95}")
    private double successRate;
    
    @Value("${app.payment.mock-gateway.processing-delay-ms:1000}")
    private long processingDelayMs;
    
    private final Random random = new Random();
    
    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + request.getOrderId()));
        
        // Check if payment already exists
        paymentRepository.findByOrderId(order.getId())
                .ifPresent(payment -> {
                    throw new IllegalArgumentException("Payment already exists for this order");
                });
        
        // Create payment record
        Payment payment = Payment.builder()
                .orderId(order.getId())
                .paymentMethod(request.getPaymentMethod())
                .amount(order.getTotalAmount())
                .status(PaymentStatus.PROCESSING)
                .build();
        
        payment = paymentRepository.save(payment);
        
        // Simulate payment processing delay
        try {
            Thread.sleep(processingDelayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Mock payment gateway processing
        boolean success = random.nextDouble() < successRate;
        
        if (success) {
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setGatewayResponse("Payment successful. Transaction ID: " + payment.getTransactionId());
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason("Payment gateway declined the transaction");
            payment.setGatewayResponse("Payment failed");
        }
        
        payment = paymentRepository.save(payment);
        
        return mapToResponse(payment);
    }
    
    public PaymentResponse getByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for order id: " + orderId));
        return mapToResponse(payment);
    }
    
    public PaymentResponse getByTransactionId(String transactionId) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with transaction id: " + transactionId));
        return mapToResponse(payment);
    }
    
    @Transactional
    public PaymentResponse refundPayment(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for order id: " + orderId));
        
        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new IllegalArgumentException("Only completed payments can be refunded");
        }
        
        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setGatewayResponse("Refund processed successfully");
        payment = paymentRepository.save(payment);
        
        return mapToResponse(payment);
    }
    
    private PaymentResponse mapToResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrderId())
                .transactionId(payment.getTransactionId())
                .status(payment.getStatus())
                .paymentMethod(payment.getPaymentMethod())
                .amount(payment.getAmount())
                .gatewayResponse(payment.getGatewayResponse())
                .failureReason(payment.getFailureReason())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}
