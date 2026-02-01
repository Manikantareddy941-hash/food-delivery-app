package com.fooddelivery.order.event;

import com.fooddelivery.common.enums.OrderStatus;
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
public class OrderEvent {
    private Long orderId;
    private String orderNumber;
    private Long customerId;
    private Long restaurantId;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private LocalDateTime timestamp;
    private String eventType; // ORDER_PLACED, ORDER_ACCEPTED, ORDER_PREPARING, etc.
}
