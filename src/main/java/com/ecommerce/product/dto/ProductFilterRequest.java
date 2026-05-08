package com.ecommerce.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for filtering products with multiple criteria
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Product filter request with multiple criteria")
public class ProductFilterRequest {

    @Schema(description = "Product categories to filter by", example = "[\"Men\", \"Women\"]")
    private List<String> categories;

    @Schema(description = "Product brands to filter by", example = "[\"Nike\", \"Adidas\"]")
    private List<String> brands;

    @Schema(description = "Product colors to filter by (by name or value)", example = "[\"black\", \"blue\"]")
    private List<String> colors;

    @Schema(description = "Minimum price filter", example = "0")
    private BigDecimal priceMin;

    @Schema(description = "Maximum price filter", example = "5000")
    private BigDecimal priceMax;

    @Schema(description = "Filter only in-stock products", example = "true")
    private Boolean inStock;

    @Schema(description = "Filter only new products", example = "false")
    private Boolean isNew;

    @Schema(description = "Filter products with free shipping", example = "false")
    private Boolean freeShipping;

    @Schema(description = "Product sizes to filter by", example = "[\"M\", \"L\"]")
    private List<String> sizes;

    @Schema(description = "Minimum rating filter (0-5)", example = "3")
    private Double minRating;

    @Schema(description = "Search query for name or description", example = "shirt")
    private String searchQuery;

    @Schema(description = "Page number for pagination", example = "0")
    private Integer page = 0;

    @Schema(description = "Page size for pagination", example = "10")
    private Integer size = 10;

    @Schema(description = "Sort field (id, name, price, createdDate, rating)", example = "price")
    private String sortBy = "id";

    @Schema(description = "Sort direction (ASC, DESC)", example = "ASC")
    private String sortDir = "ASC";
}
