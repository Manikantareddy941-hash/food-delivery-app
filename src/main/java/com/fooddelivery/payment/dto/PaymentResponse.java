package com.fooddelivery.payment.dto;

import com.fooddelivery.common.enums.PaymentMethod;
import com.fooddelivery.common.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {
    private Long id;
    private Long orderId;
    private String transactionId;
    private PaymentStatus status;
    private PaymentMethod paymentMethod;
    private BigDecimal amount;
    private String gatewayResponse;
    private String failureReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
