package com.fooddelivery.restaurant.controller;

import com.fooddelivery.common.util.SecurityUtil;
import com.fooddelivery.restaurant.dto.RestaurantRequest;
import com.fooddelivery.restaurant.dto.RestaurantResponse;
import com.fooddelivery.restaurant.service.RestaurantService;
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

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
@Tag(name = "Restaurant Management", description = "Restaurant management APIs")
public class RestaurantController {
    
    private final RestaurantService restaurantService;
    private final SecurityUtil securityUtil;
    
    @PostMapping
    @Operation(summary = "Create a new restaurant")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<RestaurantResponse> create(@Valid @RequestBody RestaurantRequest request) {
        Long ownerId = securityUtil.getCurrentUserId();
        RestaurantResponse response = restaurantService.create(request, ownerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get restaurant by ID")
    public ResponseEntity<RestaurantResponse> getById(@PathVariable Long id) {
        RestaurantResponse response = restaurantService.getById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    @Operation(summary = "Get all restaurants with pagination")
    public ResponseEntity<Page<RestaurantResponse>> getAll(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<RestaurantResponse> response = restaurantService.getAll(pageable);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/city/{city}")
    @Operation(summary = "Get restaurants by city")
    public ResponseEntity<Page<RestaurantResponse>> getByCity(
            @PathVariable String city,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<RestaurantResponse> response = restaurantService.getByCity(city, pageable);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search restaurants with filters")
    public ResponseEntity<Page<RestaurantResponse>> search(
            @RequestParam String city,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String cuisineType,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<RestaurantResponse> response = restaurantService.getByFilters(
                city, minPrice, maxPrice, cuisineType, pageable);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/my-restaurants")
    @Operation(summary = "Get restaurants owned by current user")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<Page<RestaurantResponse>> getMyRestaurants(
            @PageableDefault(size = 20) Pageable pageable) {
        Long ownerId = securityUtil.getCurrentUserId();
        Page<RestaurantResponse> response = restaurantService.getByOwner(ownerId, pageable);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update restaurant")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<RestaurantResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody RestaurantRequest request) {
        Long ownerId = securityUtil.getCurrentUserId();
        RestaurantResponse response = restaurantService.update(id, request, ownerId);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete restaurant")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Long ownerId = securityUtil.getCurrentUserId();
        restaurantService.delete(id, ownerId);
        return ResponseEntity.noContent().build();
    }
}
