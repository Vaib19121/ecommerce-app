package com.ecommerce.wishlist.repository;

import com.ecommerce.wishlist.model.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    
    Optional<Wishlist> findByUserId(Long userId);
    
    boolean existsByUserId(Long userId);
}
