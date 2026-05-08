package com.ecommerce.product.controller;

import com.ecommerce.common.response.ApiResponse;
import com.ecommerce.product.dto.*;
import com.ecommerce.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Product Management", description = "APIs for managing products")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create product", description = "Create a new product (Admin only)")
    public ResponseEntity<ApiResponse<ProductDto>> createProduct(
            @Valid @RequestBody ProductCreateRequest request) {
        ProductDto product = productService.createProduct(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product created successfully", product));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update product", description = "Update existing product (Admin only)")
    public ResponseEntity<ApiResponse<ProductDto>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateRequest request) {
        ProductDto product = productService.updateProduct(id, request);
        return ResponseEntity.ok(ApiResponse.success("Product updated successfully", product));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete product", description = "Soft delete product (Admin only)")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Product deleted successfully", null));
    }

    @GetMapping("/{id}/")
    @Operation(summary = "Get product detail", description = "Retrieve full product detail page data including images, colors, sizes, offers, specifications, reviews, rating distribution, and Q&A")
    public ResponseEntity<ApiResponse<ProductDetailDto>> getProductDetail(@PathVariable Long id) {
        ProductDetailDto detail = productService.getProductDetail(id);
        return ResponseEntity.ok(ApiResponse.success("Product detail retrieved successfully", detail));
    }

    @GetMapping
    @Operation(summary = "Get all products", description = "Retrieve all active products with pagination and sorting")
    public ResponseEntity<ApiResponse<Page<ProductDto>>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("DESC") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ProductDto> products = productService.getAllProducts(pageable);
        
        return ResponseEntity.ok(ApiResponse.success("Products retrieved successfully", products));
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get products by category", description = "Retrieve all products in a specific category")
    public ResponseEntity<ApiResponse<Page<ProductDto>>> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDto> products = productService.getProductsByCategory(categoryId, pageable);
        
        return ResponseEntity.ok(ApiResponse.success("Products retrieved successfully", products));
    }

    @GetMapping("/search")
    @Operation(summary = "Search products", description = "Search products by name or description")
    public ResponseEntity<ApiResponse<Page<ProductDto>>> searchProducts(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDto> products = productService.searchProducts(q, pageable);
        
        return ResponseEntity.ok(ApiResponse.success("Products found", products));
    }

    @PostMapping("/filter")
    @Operation(summary = "Filter products", description = "Filter products with multiple criteria (categories, brands, colors, price range, etc.)")
    public ResponseEntity<ApiResponse<Page<ProductDto>>> filterProducts(
            @Valid @RequestBody com.ecommerce.product.dto.ProductFilterRequest filterRequest) {
        
        Page<ProductDto> products = productService.filterProducts(filterRequest);
        
        return ResponseEntity.ok(ApiResponse.success("Products filtered successfully", products));
    }

    @GetMapping("/filter/options")
    @Operation(summary = "Get filter options", description = "Retrieve available filter options (categories, brands, colors, price range, etc.)")
    public ResponseEntity<ApiResponse<Object>> getFilterOptions() {
        var filterOptions = new java.util.HashMap<String, Object>();
        filterOptions.put("categories", com.ecommerce.product.config.ProductFilterConstants.CATEGORIES);
        filterOptions.put("brands", com.ecommerce.product.config.ProductFilterConstants.BRANDS);
        filterOptions.put("colors", com.ecommerce.product.config.ProductFilterConstants.COLORS);
        filterOptions.put("sizes", com.ecommerce.product.config.ProductFilterConstants.SIZES);
        filterOptions.put("priceMin", com.ecommerce.product.config.ProductFilterConstants.PRICE_MIN);
        filterOptions.put("priceMax", com.ecommerce.product.config.ProductFilterConstants.PRICE_MAX);
        
        return ResponseEntity.ok(ApiResponse.success("Filter options retrieved successfully", filterOptions));
    }
}


