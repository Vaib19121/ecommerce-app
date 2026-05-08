package com.ecommerce.product.repository;

import com.ecommerce.product.model.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Long> {

    boolean existsByNameIgnoreCase(String name);
}
