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
            throw new IllegalArgumentException("Restaurant not accepting orders");
        }

        List<OrderItem> orderItems = request.getItems().stream()
                .map(req -> {

                    MenuItem menuItem = menuItemRepository.findById(req.getMenuItemId())
                            .orElseThrow(() -> new ResourceNotFoundException("Menu item not found"));

                    if (!menuItem.getAvailable()) {
                        throw new IllegalArgumentException("Menu item unavailable");
                    }

                    BigDecimal total = menuItem.getPrice()
                            .multiply(BigDecimal.valueOf(req.getQuantity()));

                    return OrderItem.builder()
                            .menuItemId(menuItem.getId())
                            .menuItemName(menuItem.getName())
                            .quantity(req.getQuantity())
                            .unitPrice(menuItem.getPrice())
                            .totalPrice(total)
                            .build();
                })
                .collect(Collectors.toList());

        BigDecimal subtotal = orderItems.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal deliveryFee = restaurant.getDeliveryFee() == null
                ? BigDecimal.ZERO
                : restaurant.getDeliveryFee();

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

        // ✅ Correct persistence flow
        Order savedOrder = orderRepository.save(order);

        orderItems.forEach(i -> i.setOrder(savedOrder));
        orderItemRepository.saveAll(orderItems);
        savedOrder.setOrderItems(orderItems);

        publishOrderEvent(savedOrder, "ORDER_PLACED");

        return mapToResponse(savedOrder);
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

    @Transactional
    public OrderResponse updateStatus(Long id, OrderStatus newStatus, Long userId) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalArgumentException("Cannot update completed order");
        }

        if (newStatus == OrderStatus.PICKED) {
            order.setDeliveryPartnerId(userId);
        }

        order.setStatus(newStatus);
        order = orderRepository.save(order);

        publishOrderEvent(order, "ORDER_" + newStatus.name());

        return mapToResponse(order);
    }

    @Transactional
    public OrderResponse cancelOrder(Long id, Long customerId) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getCustomerId().equals(customerId)) {
            throw new IllegalArgumentException("Not allowed");
        }

        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalArgumentException("Cannot cancel");
        }

        order.setStatus(OrderStatus.CANCELLED);
        order = orderRepository.save(order);

        publishOrderEvent(order, "ORDER_CANCELLED");

        return mapToResponse(order);
    }

    private void publishOrderEvent(Order order, String type) {

        OrderEvent event = OrderEvent.builder()
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .customerId(order.getCustomerId())
                .restaurantId(order.getRestaurantId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .timestamp(LocalDateTime.now())
                .eventType(type)
                .build();

        kafkaTemplate.send(ORDER_TOPIC, event.getOrderNumber(), event);
    }

    private OrderResponse mapToResponse(Order order) {

        List<OrderItemResponse> items = order.getOrderItems().stream()
                .map(i -> OrderItemResponse.builder()
                        .id(i.getId())
                        .menuItemId(i.getMenuItemId())
                        .menuItemName(i.getMenuItemName())
                        .quantity(i.getQuantity())
                        .unitPrice(i.getUnitPrice())
                        .totalPrice(i.getTotalPrice())
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
