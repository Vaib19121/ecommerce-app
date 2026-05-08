package com.ecommerce.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    
    private Long id;
    private String name;
    private String brand;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer discountPercentage;
    private Integer stockQuantity;
    private List<String> images;
    private Boolean active;
    private Boolean isNew;
    private Long categoryId;
    private String categoryName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
