package com.fooddelivery.restaurant.entity;

import com.fooddelivery.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "menu_items", indexes = {
    @Index(name = "idx_restaurant_id", columnList = "restaurant_id"),
    @Index(name = "idx_category", columnList = "category"),
    @Index(name = "idx_available", columnList = "available")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItem extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;
    
    @Column(nullable = false, length = 200)
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(length = 100)
    private String category;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean available = true;
    
    @Column(name = "image_url", length = 500)
    private String imageUrl;
    
    @Column(name = "preparation_time_minutes")
    private Integer preparationTimeMinutes;
    
    @Column(name = "is_vegetarian")
    @Builder.Default
    private Boolean isVegetarian = false;
    
    @Column(name = "is_spicy")
    @Builder.Default
    private Boolean isSpicy = false;
}
