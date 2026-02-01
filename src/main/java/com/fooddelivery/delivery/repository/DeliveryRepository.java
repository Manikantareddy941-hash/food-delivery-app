package com.fooddelivery.delivery.repository;

import com.fooddelivery.common.enums.DeliveryStatus;
import com.fooddelivery.delivery.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    Optional<Delivery> findByOrderId(Long orderId);
    List<Delivery> findByDeliveryPartnerId(Long deliveryPartnerId);
    List<Delivery> findByStatus(DeliveryStatus status);
}
