package com.fooddelivery.restaurant.service;

import com.fooddelivery.common.enums.RestaurantStatus;
import com.fooddelivery.common.exception.ResourceNotFoundException;
import com.fooddelivery.restaurant.dto.RestaurantRequest;
import com.fooddelivery.restaurant.dto.RestaurantResponse;
import com.fooddelivery.restaurant.entity.Restaurant;
import com.fooddelivery.restaurant.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantService {
    
    private final RestaurantRepository restaurantRepository;
    
    @Transactional
    @CacheEvict(value = "restaurants", allEntries = true)
    public RestaurantResponse create(RestaurantRequest request, Long ownerId) {
        Restaurant restaurant = Restaurant.builder()
                .name(request.getName())
                .description(request.getDescription())
                .address(request.getAddress())
                .city(request.getCity())
                .pincode(request.getPincode())
                .phone(request.getPhone())
                .email(request.getEmail())
                .status(request.getStatus() != null ? request.getStatus() : RestaurantStatus.ACTIVE)
                .ownerId(ownerId)
                .estimatedDeliveryTimeMinutes(request.getEstimatedDeliveryTimeMinutes())
                .minimumOrderAmount(request.getMinimumOrderAmount())
                .deliveryFee(request.getDeliveryFee())
                .cuisineType(request.getCuisineType())
                .imageUrl(request.getImageUrl())
                .build();
        
        restaurant = restaurantRepository.save(restaurant);
        return mapToResponse(restaurant);
    }
    
    @Cacheable(value = "restaurants", key = "#id")
    public RestaurantResponse getById(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + id));
        return mapToResponse(restaurant);
    }
    
    public Page<RestaurantResponse> getAll(Pageable pageable) {
        return restaurantRepository.findAll(pageable)
                .map(this::mapToResponse);
    }
    
    public Page<RestaurantResponse> getByCity(String city, Pageable pageable) {
        return restaurantRepository.findByCityAndStatus(city, RestaurantStatus.ACTIVE, pageable)
                .map(this::mapToResponse);
    }
    
    public Page<RestaurantResponse> getByFilters(String city, BigDecimal minPrice, 
                                                  BigDecimal maxPrice, String cuisineType, 
                                                  Pageable pageable) {
        return restaurantRepository.findByFilters(city, RestaurantStatus.ACTIVE, 
                minPrice, maxPrice, cuisineType, pageable)
                .map(this::mapToResponse);
    }
    
    public Page<RestaurantResponse> getByOwner(Long ownerId, Pageable pageable) {
        List<Restaurant> restaurants = restaurantRepository.findByOwnerId(ownerId);
        // Convert to page - in production, you'd add a repository method with Pageable
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), restaurants.size());
        List<Restaurant> pagedRestaurants = start < restaurants.size() ? 
                restaurants.subList(start, end) : java.util.Collections.emptyList();
        return new PageImpl<>(
                pagedRestaurants.stream().map(this::mapToResponse).collect(Collectors.toList()),
                pageable,
                restaurants.size());
    }
    
    @Transactional
    @CacheEvict(value = "restaurants", key = "#id")
    public RestaurantResponse update(Long id, RestaurantRequest request, Long ownerId) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + id));
        
        if (!restaurant.getOwnerId().equals(ownerId)) {
            throw new IllegalArgumentException("You don't have permission to update this restaurant");
        }
        
        if (request.getName() != null) restaurant.setName(request.getName());
        if (request.getDescription() != null) restaurant.setDescription(request.getDescription());
        if (request.getAddress() != null) restaurant.setAddress(request.getAddress());
        if (request.getCity() != null) restaurant.setCity(request.getCity());
        if (request.getPincode() != null) restaurant.setPincode(request.getPincode());
        if (request.getPhone() != null) restaurant.setPhone(request.getPhone());
        if (request.getEmail() != null) restaurant.setEmail(request.getEmail());
        if (request.getStatus() != null) restaurant.setStatus(request.getStatus());
        if (request.getEstimatedDeliveryTimeMinutes() != null) 
            restaurant.setEstimatedDeliveryTimeMinutes(request.getEstimatedDeliveryTimeMinutes());
        if (request.getMinimumOrderAmount() != null) 
            restaurant.setMinimumOrderAmount(request.getMinimumOrderAmount());
        if (request.getDeliveryFee() != null) restaurant.setDeliveryFee(request.getDeliveryFee());
        if (request.getCuisineType() != null) restaurant.setCuisineType(request.getCuisineType());
        if (request.getImageUrl() != null) restaurant.setImageUrl(request.getImageUrl());
        
        restaurant = restaurantRepository.save(restaurant);
        return mapToResponse(restaurant);
    }
    
    @Transactional
    @CacheEvict(value = "restaurants", key = "#id")
    public void delete(Long id, Long ownerId) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + id));
        
        if (!restaurant.getOwnerId().equals(ownerId)) {
            throw new IllegalArgumentException("You don't have permission to delete this restaurant");
        }
        
        restaurantRepository.delete(restaurant);
    }
    
    private RestaurantResponse mapToResponse(Restaurant restaurant) {
        return RestaurantResponse.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .description(restaurant.getDescription())
                .address(restaurant.getAddress())
                .city(restaurant.getCity())
                .pincode(restaurant.getPincode())
                .phone(restaurant.getPhone())
                .email(restaurant.getEmail())
                .status(restaurant.getStatus())
                .ownerId(restaurant.getOwnerId())
                .averageRating(restaurant.getAverageRating())
                .totalRatings(restaurant.getTotalRatings())
                .estimatedDeliveryTimeMinutes(restaurant.getEstimatedDeliveryTimeMinutes())
                .minimumOrderAmount(restaurant.getMinimumOrderAmount())
                .deliveryFee(restaurant.getDeliveryFee())
                .cuisineType(restaurant.getCuisineType())
                .imageUrl(restaurant.getImageUrl())
                .build();
    }
}
