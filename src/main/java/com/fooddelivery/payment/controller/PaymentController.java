package com.fooddelivery.payment.controller;

import com.fooddelivery.payment.dto.PaymentRequest;
import com.fooddelivery.payment.dto.PaymentResponse;
import com.fooddelivery.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payment Management", description = "Payment management APIs")
@SecurityRequirement(name = "bearerAuth")
public class PaymentController {
    
    private final PaymentService paymentService;
    
    @PostMapping
    @Operation(summary = "Process payment for an order")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<PaymentResponse> processPayment(@Valid @RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.processPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get payment by order ID")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'RESTAURANT_OWNER', 'ADMIN')")
    public ResponseEntity<PaymentResponse> getByOrderId(@PathVariable Long orderId) {
        PaymentResponse response = paymentService.getByOrderId(orderId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/transaction/{transactionId}")
    @Operation(summary = "Get payment by transaction ID")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<PaymentResponse> getByTransactionId(@PathVariable String transactionId) {
        PaymentResponse response = paymentService.getByTransactionId(transactionId);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/refund/{orderId}")
    @Operation(summary = "Refund payment for an order")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentResponse> refundPayment(@PathVariable Long orderId) {
        PaymentResponse response = paymentService.refundPayment(orderId);
        return ResponseEntity.ok(response);
    }
}
