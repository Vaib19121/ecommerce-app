package com.ecommerce.order.dto;

import com.ecommerce.order.model.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderDto(
        Long id,
        String product,
        String image,
        OrderStatus status,
        BigDecimal price,
        Long userId,
        LocalDateTime date
) {}
