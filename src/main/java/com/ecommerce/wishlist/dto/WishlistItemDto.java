package com.ecommerce.wishlist.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for wishlist item response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Wishlist item with product details")
public class WishlistItemDto {

    @Schema(description = "Wishlist item ID", example = "1")
    private Long id;

    @Schema(description = "Product ID", example = "5")
    private Long productId;

    @Schema(description = "Product name", example = "Nike Air Max 90")
    private String productName;

    @Schema(description = "Product brand", example = "Nike")
    private String brand;

    @Schema(description = "Product price", example = "99.99")
    private BigDecimal price;

    @Schema(description = "Product original price", example = "120.00")
    private BigDecimal originalPrice;

    @Schema(description = "Discount percentage", example = "17")
    private Integer discountPercentage;

    @Schema(description = "Product image URL", example = "https://example.com/image.jpg")
    private String imageUrl;

    @Schema(description = "Product in stock status", example = "true")
    private Boolean inStock;

    @Schema(description = "Date added to wishlist")
    private LocalDateTime addedDate;
}
