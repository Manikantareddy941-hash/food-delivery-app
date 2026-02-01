package com.fooddelivery.restaurant.controller;

import com.fooddelivery.common.util.SecurityUtil;
import com.fooddelivery.restaurant.dto.MenuItemRequest;
import com.fooddelivery.restaurant.dto.MenuItemResponse;
import com.fooddelivery.restaurant.service.MenuItemService;
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

import java.util.List;

@RestController
@RequestMapping("/api/restaurants/{restaurantId}/menu-items")
@RequiredArgsConstructor
@Tag(name = "Menu Item Management", description = "Menu item management APIs")
public class MenuItemController {
    
    private final MenuItemService menuItemService;
    private final SecurityUtil securityUtil;
    
    @PostMapping
    @Operation(summary = "Create a new menu item")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<MenuItemResponse> create(
            @PathVariable Long restaurantId,
            @Valid @RequestBody MenuItemRequest request) {
        Long ownerId = securityUtil.getCurrentUserId();
        MenuItemResponse response = menuItemService.create(restaurantId, request, ownerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping
    @Operation(summary = "Get all menu items for a restaurant")
    public ResponseEntity<List<MenuItemResponse>> getByRestaurant(@PathVariable Long restaurantId) {
        List<MenuItemResponse> response = menuItemService.getByRestaurant(restaurantId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/available")
    @Operation(summary = "Get available menu items with pagination")
    public ResponseEntity<Page<MenuItemResponse>> getAvailable(
            @PathVariable Long restaurantId,
            @RequestParam(required = false) Boolean available,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<MenuItemResponse> response = menuItemService.getByRestaurant(restaurantId, available, pageable);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get menu item by ID")
    public ResponseEntity<MenuItemResponse> getById(@PathVariable Long id) {
        MenuItemResponse response = menuItemService.getById(id);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update menu item")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<MenuItemResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody MenuItemRequest request) {
        Long ownerId = securityUtil.getCurrentUserId();
        MenuItemResponse response = menuItemService.update(id, request, ownerId);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete menu item")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Long ownerId = securityUtil.getCurrentUserId();
        menuItemService.delete(id, ownerId);
        return ResponseEntity.noContent().build();
    }
}
