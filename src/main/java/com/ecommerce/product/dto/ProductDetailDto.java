package com.ecommerce.product.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProductDetailDto(
        Long id,
        String brand,
        String title,
        Double rating,
        Long reviewCount,
        BigDecimal originalPrice,
        BigDecimal price,
        Integer discountPercentage,
        String description,
        List<String> images,
        List<ProductColorDto> colors,
        List<String> sizes,
        Boolean inStock,
        Boolean isNew,
        String estimatedDelivery,
        Boolean freeShipping,
        SellerDto seller,
        List<ProductOfferDto> offers,
        List<ProductSpecificationDto> specifications,
        List<ProductReviewDto> reviews,
        List<RatingDistributionDto> ratingDistribution,
        List<ProductQnADto> qna
) {}
