package com.fooddelivery.order.dto;

import com.fooddelivery.common.enums.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    
    @NotNull(message = "Restaurant ID is required")
    private Long restaurantId;
    
    @Valid
    @NotNull(message = "Order items are required")
    private List<CartItemRequest> items;
    
    @NotBlank(message = "Delivery address is required")
    @Size(max = 500, message = "Delivery address must not exceed 500 characters")
    private String deliveryAddress;
    
    @NotBlank(message = "Delivery city is required")
    @Size(max = 50, message = "Delivery city must not exceed 50 characters")
    private String deliveryCity;
    
    @NotBlank(message = "Delivery pincode is required")
    @Size(max = 10, message = "Delivery pincode must not exceed 10 characters")
    private String deliveryPincode;
    
    @NotBlank(message = "Delivery phone is required")
    @Size(max = 20, message = "Delivery phone must not exceed 20 characters")
    private String deliveryPhone;
    
    @Size(max = 1000, message = "Special instructions must not exceed 1000 characters")
    private String specialInstructions;
    
    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;
}
