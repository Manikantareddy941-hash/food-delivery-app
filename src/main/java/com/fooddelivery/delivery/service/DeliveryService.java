package com.fooddelivery.delivery.service;

import com.fooddelivery.common.enums.DeliveryStatus;
import com.fooddelivery.common.exception.ResourceNotFoundException;
import com.fooddelivery.delivery.dto.DeliveryResponse;
import com.fooddelivery.delivery.entity.Delivery;
import com.fooddelivery.order.entity.Order;
import com.fooddelivery.order.repository.OrderRepository;
import com.fooddelivery.restaurant.entity.Restaurant;
import com.fooddelivery.restaurant.repository.RestaurantRepository;
import com.fooddelivery.delivery.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DeliveryService {
    
    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;
    
    @Transactional
    public DeliveryResponse createDelivery(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        
        // Check if delivery already exists
        deliveryRepository.findByOrderId(orderId)
                .ifPresent(delivery -> {
                    throw new IllegalArgumentException("Delivery already exists for this order");
                });
        
        Restaurant restaurant = restaurantRepository.findById(order.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
        
        Delivery delivery = Delivery.builder()
                .orderId(orderId)
                .status(DeliveryStatus.PENDING)
                .pickupAddress(restaurant.getAddress() + ", " + restaurant.getCity())
                .deliveryAddress(order.getDeliveryAddress())
                .estimatedDeliveryTime(LocalDateTime.now().plusMinutes(
                        restaurant.getEstimatedDeliveryTimeMinutes() != null ? 
                        restaurant.getEstimatedDeliveryTimeMinutes() : 30))
                .trackingUrl("https://track.fooddelivery.com/" + order.getOrderNumber())
                .build();
        
        delivery = deliveryRepository.save(delivery);
        return mapToResponse(delivery);
    }
    
    @Transactional
    public DeliveryResponse assignDeliveryPartner(Long orderId, Long deliveryPartnerId) {
        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found for order id: " + orderId));
        
        if (delivery.getStatus() != DeliveryStatus.PENDING) {
            throw new IllegalArgumentException("Delivery is already assigned or in progress");
        }
        
        delivery.setDeliveryPartnerId(deliveryPartnerId);
        delivery.setStatus(DeliveryStatus.ASSIGNED);
        delivery = deliveryRepository.save(delivery);
        
        // Update order with delivery partner
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        order.setDeliveryPartnerId(deliveryPartnerId);
        orderRepository.save(order);
        
        return mapToResponse(delivery);
    }
    
    @Transactional
    public DeliveryResponse updateStatus(Long orderId, DeliveryStatus status, Long deliveryPartnerId) {
        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found for order id: " + orderId));
        
        if (delivery.getDeliveryPartnerId() != null && 
            !delivery.getDeliveryPartnerId().equals(deliveryPartnerId)) {
            throw new IllegalArgumentException("You don't have permission to update this delivery");
        }
        
        delivery.setStatus(status);
        
        if (status == DeliveryStatus.DELIVERED) {
            delivery.setActualDeliveryTime(LocalDateTime.now());
        }
        
        delivery = deliveryRepository.save(delivery);
        return mapToResponse(delivery);
    }
    
    public DeliveryResponse getByOrderId(Long orderId) {
        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found for order id: " + orderId));
        return mapToResponse(delivery);
    }
    
    @KafkaListener(topics = "order-events", groupId = "delivery-service")
    public void handleOrderEvent(com.fooddelivery.order.event.OrderEvent event) {
        if ("ORDER_ACCEPTED".equals(event.getEventType())) {
            // Auto-create delivery when order is accepted
            try {
                createDelivery(event.getOrderId());
            } catch (Exception e) {
                // Log error - delivery might already exist
            }
        }
    }
    
    private DeliveryResponse mapToResponse(Delivery delivery) {
        return DeliveryResponse.builder()
                .id(delivery.getId())
                .orderId(delivery.getOrderId())
                .deliveryPartnerId(delivery.getDeliveryPartnerId())
                .status(delivery.getStatus())
                .pickupAddress(delivery.getPickupAddress())
                .deliveryAddress(delivery.getDeliveryAddress())
                .estimatedDeliveryTime(delivery.getEstimatedDeliveryTime())
                .actualDeliveryTime(delivery.getActualDeliveryTime())
                .trackingUrl(delivery.getTrackingUrl())
                .createdAt(delivery.getCreatedAt())
                .updatedAt(delivery.getUpdatedAt())
                .build();
    }
}
