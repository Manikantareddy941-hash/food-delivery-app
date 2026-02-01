package com.fooddelivery.order.controller;

import com.fooddelivery.common.enums.OrderStatus;
import com.fooddelivery.common.util.SecurityUtil;
import com.fooddelivery.order.dto.OrderRequest;
import com.fooddelivery.order.dto.OrderResponse;
import com.fooddelivery.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Order Management", description = "Order management APIs")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {
    
    private final OrderService orderService;
    private final SecurityUtil securityUtil;
    
    @PostMapping
    @Operation(summary = "Place a new order")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderResponse> placeOrder(@Valid @RequestBody OrderRequest request) {
        Long customerId = securityUtil.getCurrentUserId();
        OrderResponse response = orderService.placeOrder(request, customerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'RESTAURANT_OWNER', 'DELIVERY_PARTNER', 'ADMIN')")
    public ResponseEntity<OrderResponse> getById(@PathVariable Long id) {
        OrderResponse response = orderService.getById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/order-number/{orderNumber}")
    @Operation(summary = "Get order by order number")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'RESTAURANT_OWNER', 'DELIVERY_PARTNER', 'ADMIN')")
    public ResponseEntity<OrderResponse> getByOrderNumber(@PathVariable String orderNumber) {
        OrderResponse response = orderService.getByOrderNumber(orderNumber);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/my-orders")
    @Operation(summary = "Get orders for current customer")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Page<OrderResponse>> getMyOrders(
            @PageableDefault(size = 20) Pageable pageable) {
        Long customerId = securityUtil.getCurrentUserId();
        Page<OrderResponse> response = orderService.getByCustomer(customerId, pageable);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/restaurant/{restaurantId}")
    @Operation(summary = "Get orders for a restaurant")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<Page<OrderResponse>> getRestaurantOrders(
            @PathVariable Long restaurantId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<OrderResponse> response = orderService.getByRestaurant(restaurantId, pageable);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}/status")
    @Operation(summary = "Update order status")
    @PreAuthorize("hasAnyRole('RESTAURANT_OWNER', 'DELIVERY_PARTNER', 'ADMIN')")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {
        Long userId = securityUtil.getCurrentUserId();
        OrderResponse response = orderService.updateStatus(id, status, userId);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancel order")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long id) {
        Long customerId = securityUtil.getCurrentUserId();
        OrderResponse response = orderService.cancelOrder(id, customerId);
        return ResponseEntity.ok(response);
    }
}
