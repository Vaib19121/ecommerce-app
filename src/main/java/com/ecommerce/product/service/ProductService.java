package com.ecommerce.product.service;

import com.ecommerce.product.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    
    ProductDto createProduct(ProductCreateRequest request);
    
    ProductDto updateProduct(Long id, ProductUpdateRequest request);
    
    void deleteProduct(Long id);
    
    ProductDto getProductById(Long id);

    ProductDetailDto getProductDetail(Long id);
    
    Page<ProductDto> getAllProducts(Pageable pageable);
    
    Page<ProductDto> getProductsByCategory(Long categoryId, Pageable pageable);
    
    Page<ProductDto> searchProducts(String query, Pageable pageable);

    Page<ProductDto> filterProducts(ProductFilterRequest filterRequest);
}
