package com.ecommerce.product.dto;

public record ProductSpecificationDto(
        Long id,
        String label,
        String value,
        Integer displayOrder
) {}
