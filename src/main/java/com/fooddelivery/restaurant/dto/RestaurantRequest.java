package com.fooddelivery.restaurant.dto;

import com.fooddelivery.common.enums.RestaurantStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RestaurantRequest {
    
    @NotBlank(message = "Name is required")
    @Size(max = 200, message = "Name must not exceed 200 characters")
    private String name;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    
    @NotBlank(message = "Address is required")
    @Size(max = 200, message = "Address must not exceed 200 characters")
    private String address;
    
    @NotBlank(message = "City is required")
    @Size(max = 50, message = "City must not exceed 50 characters")
    private String city;
    
    @NotBlank(message = "Pincode is required")
    @Size(max = 10, message = "Pincode must not exceed 10 characters")
    private String pincode;
    
    @NotBlank(message = "Phone is required")
    @Size(max = 20, message = "Phone must not exceed 20 characters")
    private String phone;
    
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;
    
    private RestaurantStatus status;
    
    private Integer estimatedDeliveryTimeMinutes;
    
    private BigDecimal minimumOrderAmount;
    
    private BigDecimal deliveryFee;
    
    @Size(max = 100, message = "Cuisine type must not exceed 100 characters")
    private String cuisineType;
    
    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    private String imageUrl;
}
