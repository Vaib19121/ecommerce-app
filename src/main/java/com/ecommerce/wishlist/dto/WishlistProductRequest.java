package com.ecommerce.wishlist.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO to add/remove product to/from wishlist
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to add or remove product from wishlist")
public class WishlistProductRequest {

    @NotNull(message = "Product ID is required")
    @Schema(description = "Product ID to add or remove", example = "5")
    private Long productId;
}
