package com.fooddelivery.delivery.entity;

import com.fooddelivery.common.entity.BaseEntity;
import com.fooddelivery.common.enums.DeliveryStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "deliveries", indexes = {
    @Index(name = "idx_order_id", columnList = "order_id"),
    @Index(name = "idx_delivery_partner_id", columnList = "delivery_partner_id"),
    @Index(name = "idx_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Delivery extends BaseEntity {
    
    @Column(name = "order_id", nullable = false, unique = true)
    private Long orderId;
    
    @Column(name = "delivery_partner_id")
    private Long deliveryPartnerId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private DeliveryStatus status = DeliveryStatus.PENDING;
    
    @Column(name = "pickup_address", length = 500)
    private String pickupAddress;
    
    @Column(name = "delivery_address", nullable = false, length = 500)
    private String deliveryAddress;
    
    @Column(name = "estimated_delivery_time")
    private java.time.LocalDateTime estimatedDeliveryTime;
    
    @Column(name = "actual_delivery_time")
    private java.time.LocalDateTime actualDeliveryTime;
    
    @Column(name = "tracking_url", length = 500)
    private String trackingUrl;
}
