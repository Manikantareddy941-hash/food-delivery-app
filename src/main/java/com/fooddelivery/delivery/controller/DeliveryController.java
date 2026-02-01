package com.fooddelivery.delivery.controller;

import com.fooddelivery.common.enums.DeliveryStatus;
import com.fooddelivery.common.util.SecurityUtil;
import com.fooddelivery.delivery.dto.DeliveryResponse;
import com.fooddelivery.delivery.service.DeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
@Tag(name = "Delivery Management", description = "Delivery management APIs")
@SecurityRequirement(name = "bearerAuth")
public class DeliveryController {
    
    private final DeliveryService deliveryService;
    private final SecurityUtil securityUtil;
    
    @PostMapping("/order/{orderId}")
    @Operation(summary = "Create delivery for an order")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DeliveryResponse> createDelivery(@PathVariable Long orderId) {
        DeliveryResponse response = deliveryService.createDelivery(orderId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/order/{orderId}/assign")
    @Operation(summary = "Assign delivery partner to an order")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DeliveryResponse> assignDeliveryPartner(
            @PathVariable Long orderId,
            @RequestParam Long deliveryPartnerId) {
        DeliveryResponse response = deliveryService.assignDeliveryPartner(orderId, deliveryPartnerId);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/order/{orderId}/status")
    @Operation(summary = "Update delivery status")
    @PreAuthorize("hasRole('DELIVERY_PARTNER')")
    public ResponseEntity<DeliveryResponse> updateStatus(
            @PathVariable Long orderId,
            @RequestParam DeliveryStatus status) {
        Long deliveryPartnerId = securityUtil.getCurrentUserId();
        DeliveryResponse response = deliveryService.updateStatus(orderId, status, deliveryPartnerId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get delivery by order ID")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'RESTAURANT_OWNER', 'DELIVERY_PARTNER', 'ADMIN')")
    public ResponseEntity<DeliveryResponse> getByOrderId(@PathVariable Long orderId) {
        DeliveryResponse response = deliveryService.getByOrderId(orderId);
        return ResponseEntity.ok(response);
    }
}
