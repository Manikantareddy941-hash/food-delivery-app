package com.fooddelivery.payment.dto;

import com.fooddelivery.common.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentRequest {
    
    @NotNull(message = "Order ID is required")
    private Long orderId;
    
    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;
}
