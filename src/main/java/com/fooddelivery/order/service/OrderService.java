package com.fooddelivery.order.service;

import com.fooddelivery.common.enums.OrderStatus;
import com.fooddelivery.common.enums.RestaurantStatus;
import com.fooddelivery.common.exception.ResourceNotFoundException;
import com.fooddelivery.order.dto.OrderItemResponse;
import com.fooddelivery.order.dto.OrderRequest;
import com.fooddelivery.order.dto.OrderResponse;
import com.fooddelivery.order.entity.Order;
import com.fooddelivery.order.entity.OrderItem;
import com.fooddelivery.order.event.OrderEvent;
import com.fooddelivery.order.repository.OrderItemRepository;
import com.fooddelivery.order.repository.OrderRepository;
import com.fooddelivery.restaurant.entity.MenuItem;
import com.fooddelivery.restaurant.entity.Restaurant;
import com.fooddelivery.restaurant.repository.MenuItemRepository;
import com.fooddelivery.restaurant.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String ORDER_TOPIC = "order-events";
    private static final BigDecimal TAX_RATE = new BigDecimal("0.18");

    @Transactional
    public OrderResponse placeOrder(OrderRequest request, Long customerId) {

        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

        if (restaurant.getStatus() != RestaurantStatus.ACTIVE) {
            throw new IllegalArgumentException("Restaurant is not available for orders");
        }

        List<OrderItem> orderItems = request.getItems().stream()
                .map(itemRequest -> {

                    MenuItem menuItem = menuItemRepository.findById(itemRequest.getMenuItemId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("Menu item not found"));

                    if (!menuItem.getAvailable()) {
                        throw new IllegalArgumentException("Menu item not available");
                    }

                    BigDecimal itemTotal = menuItem.getPrice()
                            .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

                    return OrderItem.builder()
                            .menuItemId(menuItem.getId())
                            .menuItemName(menuItem.getName())
                            .quantity(itemRequest.getQuantity())
                            .unitPrice(menuItem.getPrice())
                            .totalPrice(itemTotal)
                            .build();
                })
                .collect(Collectors.toList());

        BigDecimal subtotal = orderItems.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal deliveryFee = restaurant.getDeliveryFee() != null
                ? restaurant.getDeliveryFee()
                : BigDecimal.ZERO;

        BigDecimal tax = subtotal.multiply(TAX_RATE);
        BigDecimal totalAmount = subtotal.add(deliveryFee).add(tax);

        Order order = Order.builder()
                .customerId(customerId)
                .restaurantId(restaurant.getId())
                .status(OrderStatus.PLACED)
                .subtotal(subtotal)
                .deliveryFee(deliveryFee)
                .tax(tax)
                .totalAmount(totalAmount)
                .deliveryAddress(request.getDeliveryAddress())
                .deliveryCity(request.getDeliveryCity())
                .deliveryPincode(request.getDeliveryPincode())
                .deliveryPhone(request.getDeliveryPhone())
                .specialInstructions(request.getSpecialInstructions())
                .build();

        order = orderRepository.save(order);

        orderItems.forEach(item -> item.setOrder(order));
        orderItemRepository.saveAll(orderItems);
        order.setOrderItems(orderItems);

        publishOrderEvent(order, "ORDER_PLACED");

        return mapToResponse(order);
    }

    public OrderResponse getById(Long id) {
        return orderRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    public OrderResponse getByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    public Page<OrderResponse> getByCustomer(Long customerId, Pageable pageable) {
        return orderRepository.findByCustomerId(customerId, pageable)
                .map(this::mapToResponse);
    }

    public Page<OrderResponse> getByRestaurant(Long restaurantId, Pageable pageable) {
        return orderRepository.findByRestaurantId(restaurantId, pageable)
                .map(this::mapToResponse);
    }

    private void publishOrderEvent(Order order, String eventType) {

        OrderEvent event = OrderEvent.builder()
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .customerId(order.getCustomerId())
                .restaurantId(order.getRestaurantId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .timestamp(LocalDateTime.now())
                .eventType(eventType)
                .build();

        kafkaTemplate.send(ORDER_TOPIC, event.getOrderNumber(), event);
    }

    private OrderResponse mapToResponse(Order order) {

        List<OrderItemResponse> items = order.getOrderItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .id(item.getId())
                        .menuItemId(item.getMenuItemId())
                        .menuItemName(item.getMenuItemName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .totalPrice(item.getTotalPrice())
                        .build())
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .customerId(order.getCustomerId())
                .restaurantId(order.getRestaurantId())
                .status(order.getStatus())
                .subtotal(order.getSubtotal())
                .deliveryFee(order.getDeliveryFee())
                .tax(order.getTax())
                .totalAmount(order.getTotalAmount())
                .deliveryAddress(order.getDeliveryAddress())
                .deliveryCity(order.getDeliveryCity())
                .deliveryPincode(order.getDeliveryPincode())
                .deliveryPhone(order.getDeliveryPhone())
                .specialInstructions(order.getSpecialInstructions())
                .deliveryPartnerId(order.getDeliveryPartnerId())
                .orderItems(items)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}
