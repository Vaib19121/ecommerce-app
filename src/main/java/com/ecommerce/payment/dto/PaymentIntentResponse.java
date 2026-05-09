package com.ecommerce.payment.dto;

import java.math.BigDecimal;

public record PaymentIntentResponse(
        String clientSecret,
        String paymentIntentId,
        Long orderId,
        BigDecimal amount,
        String currency
) {}
