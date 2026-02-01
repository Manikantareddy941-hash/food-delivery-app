package com.fooddelivery.restaurant.dto;

import com.fooddelivery.common.enums.RestaurantStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantResponse {
    private Long id;
    private String name;
    private String description;
    private String address;
    private String city;
    private String pincode;
    private String phone;
    private String email;
    private RestaurantStatus status;
    private Long ownerId;
    private BigDecimal averageRating;
    private Integer totalRatings;
    private Integer estimatedDeliveryTimeMinutes;
    private BigDecimal minimumOrderAmount;
    private BigDecimal deliveryFee;
    private String cuisineType;
    private String imageUrl;
}
