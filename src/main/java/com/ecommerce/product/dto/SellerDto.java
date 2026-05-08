package com.ecommerce.product.dto;

import java.math.BigDecimal;

public record SellerDto(
        Long id,
        String name,
        BigDecimal rating
) {}
