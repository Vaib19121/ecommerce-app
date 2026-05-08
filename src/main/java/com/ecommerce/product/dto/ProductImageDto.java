package com.ecommerce.product.dto;

public record ProductImageDto(
        Long id,
        String url,
        Integer displayOrder
) {}
