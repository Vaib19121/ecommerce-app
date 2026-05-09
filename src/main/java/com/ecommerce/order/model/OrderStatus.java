package com.ecommerce.order.model;

public enum OrderStatus {
    PENDING,      // Payment initiated, not yet confirmed
    IN_TRANSIT,   // Payment successful, order being shipped
    DELIVERED,    // Order delivered
    CANCELLED     // Payment failed or order cancelled
}
