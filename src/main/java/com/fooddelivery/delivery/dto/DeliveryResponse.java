package com.fooddelivery.delivery.dto;

import com.fooddelivery.common.enums.DeliveryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryResponse {
    private Long id;
    private Long orderId;
    private Long deliveryPartnerId;
    private DeliveryStatus status;
    private String pickupAddress;
    private String deliveryAddress;
    private LocalDateTime estimatedDeliveryTime;
    private LocalDateTime actualDeliveryTime;
    private String trackingUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
