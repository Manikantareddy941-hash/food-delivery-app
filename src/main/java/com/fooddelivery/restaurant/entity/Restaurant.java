package com.fooddelivery.restaurant.entity;

import com.fooddelivery.common.entity.BaseEntity;
import com.fooddelivery.common.enums.RestaurantStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "restaurants", indexes = {
    @Index(name = "idx_owner_id", columnList = "owner_id"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_city", columnList = "city")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Restaurant extends BaseEntity {
    
    @Column(nullable = false, length = 200)
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    @Column(nullable = false, length = 200)
    private String address;
    
    @Column(nullable = false, length = 50)
    private String city;
    
    @Column(nullable = false, length = 10)
    private String pincode;
    
    @Column(nullable = false, length = 20)
    private String phone;
    
    @Column(length = 100)
    private String email;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private RestaurantStatus status = RestaurantStatus.ACTIVE;
    
    @Column(name = "owner_id", nullable = false)
    private Long ownerId;
    
    @Column(name = "average_rating", precision = 3, scale = 2)
    @Builder.Default
    private BigDecimal averageRating = BigDecimal.ZERO;
    
    @Column(name = "total_ratings")
    @Builder.Default
    private Integer totalRatings = 0;
    
    @Column(name = "estimated_delivery_time_minutes")
    private Integer estimatedDeliveryTimeMinutes;
    
    @Column(name = "minimum_order_amount", precision = 10, scale = 2)
    private BigDecimal minimumOrderAmount;
    
    @Column(name = "delivery_fee", precision = 10, scale = 2)
    private BigDecimal deliveryFee;
    
    @Column(name = "cuisine_type", length = 100)
    private String cuisineType;
    
    @Column(name = "image_url", length = 500)
    private String imageUrl;
    
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MenuItem> menuItems = new ArrayList<>();
}
