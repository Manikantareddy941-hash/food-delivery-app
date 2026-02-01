package com.fooddelivery.restaurant.service;

import com.fooddelivery.common.exception.ResourceNotFoundException;
import com.fooddelivery.restaurant.dto.MenuItemRequest;
import com.fooddelivery.restaurant.dto.MenuItemResponse;
import com.fooddelivery.restaurant.entity.MenuItem;
import com.fooddelivery.restaurant.entity.Restaurant;
import com.fooddelivery.restaurant.repository.MenuItemRepository;
import com.fooddelivery.restaurant.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;

    @Transactional
    @CacheEvict(value = "menus", key = "#restaurantId")
    public MenuItemResponse create(Long restaurantId, MenuItemRequest request, Long ownerId) {

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + restaurantId));

        if (!restaurant.getOwnerId().equals(ownerId)) {
            throw new IllegalArgumentException("You don't have permission to add menu items");
        }

        MenuItem menuItem = MenuItem.builder()
                .restaurant(restaurant)
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .category(request.getCategory())
                .available(request.getAvailable() != null ? request.getAvailable() : true)
                .imageUrl(request.getImageUrl())
                .preparationTimeMinutes(request.getPreparationTimeMinutes())
                .isVegetarian(request.getIsVegetarian() != null && request.getIsVegetarian())
                .isSpicy(request.getIsSpicy() != null && request.getIsSpicy())
                .build();

        return mapToResponse(menuItemRepository.save(menuItem));
    }

    @Cacheable(value = "menus", key = "#restaurantId")
    public List<MenuItemResponse> getByRestaurant(Long restaurantId) {

        return menuItemRepository.findByRestaurantId(restaurantId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public Page<MenuItemResponse> getByRestaurant(Long restaurantId, Boolean available, Pageable pageable) {

        if (available != null) {
            return menuItemRepository
                    .findByRestaurantIdAndAvailable(restaurantId, available, pageable)
                    .map(this::mapToResponse);
        }

        return menuItemRepository
                .findByRestaurantId(restaurantId, pageable)
                .map(this::mapToResponse);
    }

    public MenuItemResponse getById(Long id) {

        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found: " + id));

        return mapToResponse(menuItem);
    }

    @Transactional
    @CacheEvict(value = "menus", key = "#menuItem.restaurant.id")
    public MenuItemResponse update(Long id, MenuItemRequest request, Long ownerId) {

        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found: " + id));

        Restaurant restaurant = menuItem.getRestaurant();
        if (!restaurant.getOwnerId().equals(ownerId)) {
            throw new IllegalArgumentException("Not allowed to update this item");
        }

        if (request.getName() != null) menuItem.setName(request.getName());
        if (request.getDescription() != null) menuItem.setDescription(request.getDescription());
        if (request.getPrice() != null) menuItem.setPrice(request.getPrice());
        if (request.getCategory() != null) menuItem.setCategory(request.getCategory());
        if (request.getAvailable() != null) menuItem.setAvailable(request.getAvailable());
        if (request.getImageUrl() != null) menuItem.setImageUrl(request.getImageUrl());
        if (request.getPreparationTimeMinutes() != null)
            menuItem.setPreparationTimeMinutes(request.getPreparationTimeMinutes());
        if (request.getIsVegetarian() != null) menuItem.setIsVegetarian(request.getIsVegetarian());
        if (request.getIsSpicy() != null) menuItem.setIsSpicy(request.getIsSpicy());

        return mapToResponse(menuItemRepository.save(menuItem));
    }

    @Transactional
    @CacheEvict(value = "menus", key = "#menuItem.restaurant.id")
    public void delete(Long id, Long ownerId) {

        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found: " + id));

        if (!menuItem.getRestaurant().getOwnerId().equals(ownerId)) {
            throw new IllegalArgumentException("Not allowed to delete this item");
        }

        menuItemRepository.delete(menuItem);
    }

    private MenuItemResponse mapToResponse(MenuItem menuItem) {

        return MenuItemResponse.builder()
                .id(menuItem.getId())
                .restaurantId(menuItem.getRestaurant().getId())
                .name(menuItem.getName())
                .description(menuItem.getDescription())
                .price(menuItem.getPrice())
                .category(menuItem.getCategory())
                .available(menuItem.getAvailable())
                .imageUrl(menuItem.getImageUrl())
                .preparationTimeMinutes(menuItem.getPreparationTimeMinutes())
                .isVegetarian(menuItem.getIsVegetarian())
                .isSpicy(menuItem.getIsSpicy())
                .build();
    }
}
