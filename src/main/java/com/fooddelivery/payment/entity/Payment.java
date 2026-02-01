package com.fooddelivery.payment.entity;

import com.fooddelivery.common.entity.BaseEntity;
import com.fooddelivery.common.enums.PaymentMethod;
import com.fooddelivery.common.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "payments", indexes = {
    @Index(name = "idx_order_id", columnList = "order_id"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_transaction_id", columnList = "transaction_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment extends BaseEntity {
    
    @Column(name = "order_id", nullable = false)
    private Long orderId;
    
    @Column(name = "transaction_id", unique = true, length = 100)
    private String transactionId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 20)
    private PaymentMethod paymentMethod;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    @Column(name = "gateway_response", length = 1000)
    private String gatewayResponse;
    
    @Column(name = "failure_reason", length = 500)
    private String failureReason;
    
    @PrePersist
    public void generateTransactionId() {
        if (transactionId == null) {
            transactionId = "TXN" + System.currentTimeMillis();
        }
    }
}
