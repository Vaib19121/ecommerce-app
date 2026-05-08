package com.ecommerce.product.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateRequest {
    
    @NotBlank(message = "Product name is required")
    private String name;

    @Size(max = 100, message = "Brand cannot exceed 100 characters")
    private String brand;
    
    private String description;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;

    @DecimalMin(value = "0.01", message = "Original price must be greater than 0")
    private BigDecimal originalPrice;

    @Min(value = 0, message = "Discount percentage must be non-negative")
    @Max(value = 100, message = "Discount percentage cannot exceed 100")
    private Integer discountPercentage;
    
    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity must be non-negative")
    private Integer stockQuantity;

    private Boolean isNew = false;

    @Size(max = 50, message = "Estimated delivery cannot exceed 50 characters")
    private String estimatedDelivery;

    private Boolean freeShipping = false;
    
    @NotNull(message = "Category ID is required")
    private Long categoryId;

    private Long sellerId;
}
