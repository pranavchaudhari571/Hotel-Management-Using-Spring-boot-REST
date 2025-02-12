package com.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    private Long reservationId;
    private BigDecimal amount;
    private String paymentMethod; // e.g., Credit Card, PayPal

    // Getters and Setters
}

