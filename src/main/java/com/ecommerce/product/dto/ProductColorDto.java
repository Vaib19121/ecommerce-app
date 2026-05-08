package com.ecommerce.product.dto;

public record ProductColorDto(
        Long id,
        String name,
        String hex,
        Boolean available
) {}
