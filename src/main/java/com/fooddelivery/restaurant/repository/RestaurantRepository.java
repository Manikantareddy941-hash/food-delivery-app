package com.fooddelivery.restaurant.repository;

import com.fooddelivery.common.enums.RestaurantStatus;
import com.fooddelivery.restaurant.entity.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    Page<Restaurant> findByStatus(RestaurantStatus status, Pageable pageable);
    Page<Restaurant> findByCityAndStatus(String city, RestaurantStatus status, Pageable pageable);
    List<Restaurant> findByOwnerId(Long ownerId);
    
    @Query("SELECT r FROM Restaurant r WHERE r.city = :city AND r.status = :status " +
           "AND (:minPrice IS NULL OR r.minimumOrderAmount >= :minPrice) " +
           "AND (:maxPrice IS NULL OR r.minimumOrderAmount <= :maxPrice) " +
           "AND (:cuisineType IS NULL OR r.cuisineType = :cuisineType)")
    Page<Restaurant> findByFilters(@Param("city") String city,
                                   @Param("status") RestaurantStatus status,
                                   @Param("minPrice") BigDecimal minPrice,
                                   @Param("maxPrice") BigDecimal maxPrice,
                                   @Param("cuisineType") String cuisineType,
                                   Pageable pageable);
}
