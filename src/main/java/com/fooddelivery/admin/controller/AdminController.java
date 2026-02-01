package com.fooddelivery.admin.controller;

import com.fooddelivery.common.enums.OrderStatus;
import com.fooddelivery.common.enums.RestaurantStatus;
import com.fooddelivery.order.dto.OrderResponse;
import com.fooddelivery.order.service.OrderService;
import com.fooddelivery.restaurant.dto.RestaurantResponse;
import com.fooddelivery.restaurant.service.RestaurantService;
import com.fooddelivery.user.dto.UserResponse;
import com.fooddelivery.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin Dashboard", description = "Admin dashboard APIs")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    private final UserService userService;
    private final RestaurantService restaurantService;
    private final OrderService orderService;
    
    // User Management
    @GetMapping("/users")
    @Operation(summary = "Get all users with pagination")
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @PageableDefault(size = 20) Pageable pageable) {
        // This would need a new method in UserService to get all users
        return ResponseEntity.ok(Page.empty());
    }
    
    @GetMapping("/users/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse response = userService.getById(id);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/users/{id}/activate")
    @Operation(summary = "Activate user account")
    public ResponseEntity<UserResponse> activateUser(@PathVariable Long id) {
        // Implementation needed in UserService
        return ResponseEntity.ok(null);
    }
    
    @PutMapping("/users/{id}/deactivate")
    @Operation(summary = "Deactivate user account")
    public ResponseEntity<UserResponse> deactivateUser(@PathVariable Long id) {
        // Implementation needed in UserService
        return ResponseEntity.ok(null);
    }
    
    // Restaurant Management
    @GetMapping("/restaurants")
    @Operation(summary = "Get all restaurants with pagination")
    public ResponseEntity<Page<RestaurantResponse>> getAllRestaurants(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<RestaurantResponse> response = restaurantService.getAll(pageable);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/restaurants/{id}/status")
    @Operation(summary = "Update restaurant status")
    public ResponseEntity<RestaurantResponse> updateRestaurantStatus(
            @PathVariable Long id,
            @RequestParam RestaurantStatus status) {
        // Implementation needed - would need to modify RestaurantService
        return ResponseEntity.ok(null);
    }
    
    // Order Management
    @GetMapping("/orders")
    @Operation(summary = "Get all orders with pagination")
    public ResponseEntity<Page<OrderResponse>> getAllOrders(
            @RequestParam(required = false) OrderStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        // Implementation needed in OrderService
        return ResponseEntity.ok(Page.empty());
    }
    
    @GetMapping("/orders/{id}")
    @Operation(summary = "Get order by ID")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        OrderResponse response = orderService.getById(id);
        return ResponseEntity.ok(response);
    }
    
    // Reports
    @GetMapping("/reports/summary")
    @Operation(summary = "Get dashboard summary")
    public ResponseEntity<Object> getDashboardSummary() {
        // Implementation for dashboard summary
        return ResponseEntity.ok(new Object() {
            public final long totalUsers = 0;
            public final long totalRestaurants = 0;
            public final long totalOrders = 0;
            public final double totalRevenue = 0.0;
        });
    }
}
