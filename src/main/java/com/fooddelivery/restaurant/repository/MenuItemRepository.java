package com.fooddelivery.restaurant.repository;

import com.fooddelivery.restaurant.entity.MenuItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    // Used for non-paged menu listing (cacheable)
    List<MenuItem> findByRestaurantId(Long restaurantId);

    // ✅ REQUIRED for pagination (this fixes your compile error)
    Page<MenuItem> findByRestaurantId(Long restaurantId, Pageable pageable);

    // Used when filtering by availability
    Page<MenuItem> findByRestaurantIdAndAvailable(
            Long restaurantId,
            Boolean available,
            Pageable pageable
    );

    // Optional category filter (fine to keep)
    List<MenuItem> findByRestaurantIdAndCategory(Long restaurantId, String category);
}
