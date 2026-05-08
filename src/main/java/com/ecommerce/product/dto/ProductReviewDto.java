package com.ecommerce.product.dto;

import java.time.LocalDate;

public record ProductReviewDto(
        Long id,
        String reviewerName,
        String avatarInitials,
        Integer rating,
        LocalDate reviewDate,
        String comment,
        Integer helpfulCount
) {}
