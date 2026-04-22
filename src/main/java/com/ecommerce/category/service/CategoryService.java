package com.ecommerce.category.service;

import com.ecommerce.category.dto.CategoryDto;
import com.ecommerce.category.dto.CategoryRequest;

import java.util.List;

public interface CategoryService {
    
    CategoryDto createCategory(CategoryRequest request);
    
    CategoryDto updateCategory(Long id, CategoryRequest request);
    
    void deleteCategory(Long id);
    
    CategoryDto getCategoryById(Long id);
    
    List<CategoryDto> getAllCategories();
    
    List<CategoryDto> searchCategories(String name);
}
