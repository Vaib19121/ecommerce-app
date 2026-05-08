package com.ecommerce.wishlist.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for complete wishlist with all items
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "User's complete wishlist")
public class WishlistDto {

    @Schema(description = "Wishlist ID", example = "1")
    private Long id;

    @Schema(description = "Total number of items in wishlist", example = "5")
    private Integer totalItems;

    @Schema(description = "List of wishlist items")
    private List<WishlistItemDto> items;

    @Schema(description = "Date wishlist was created")
    private LocalDateTime createdDate;

    @Schema(description = "Date wishlist was last modified")
    private LocalDateTime lastModifiedDate;
}
