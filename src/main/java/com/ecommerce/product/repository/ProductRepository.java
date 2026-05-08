package com.ecommerce.product.repository;

import com.ecommerce.product.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    
    Page<Product> findByActiveTrue(Pageable pageable);
    
    Page<Product> findByCategoryIdAndActiveTrue(Long categoryId, Pageable pageable);
    
    Page<Product> findByNameContainingIgnoreCaseAndActiveTrue(String name, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.active = true AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Product> searchProducts(@Param("query") String query, Pageable pageable);
    
    List<Product> findByCategoryId(Long categoryId);
}
