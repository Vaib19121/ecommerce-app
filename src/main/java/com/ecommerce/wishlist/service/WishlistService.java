package com.ecommerce.wishlist.service;

import com.ecommerce.wishlist.dto.WishlistDto;
import com.ecommerce.wishlist.dto.WishlistItemDto;

/**
 * Service interface for wishlist operations
 */
public interface WishlistService {

    /**
     * Get user's wishlist
     */
    WishlistDto getWishlist(Long userId);

    /**
     * Add product to wishlist
     */
    WishlistItemDto addProductToWishlist(Long userId, Long productId);

    /**
     * Remove product from wishlist
     */
    void removeProductFromWishlist(Long userId, Long productId);

    /**
     * Check if product is in user's wishlist
     */
    boolean isProductInWishlist(Long userId, Long productId);

    /**
     * Clear entire wishlist
     */
    void clearWishlist(Long userId);

    /**
     * Get number of items in wishlist
     */
    Integer getWishlistItemCount(Long userId);
}
