package com.fooddelivery.restaurant.repository;

import com.fooddelivery.restaurant.entity.MenuItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findByRestaurantId(Long restaurantId);
    Page<MenuItem> findByRestaurantIdAndAvailable(Long restaurantId, Boolean available, Pageable pageable);
    List<MenuItem> findByRestaurantIdAndCategory(Long restaurantId, String category);
}
