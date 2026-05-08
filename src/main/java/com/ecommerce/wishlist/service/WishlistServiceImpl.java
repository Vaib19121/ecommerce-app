package com.ecommerce.wishlist.service;

import com.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.product.model.Product;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.user.model.User;
import com.ecommerce.user.repository.UserRepository;
import com.ecommerce.wishlist.dto.WishlistDto;
import com.ecommerce.wishlist.dto.WishlistItemDto;
import com.ecommerce.wishlist.mapper.WishlistMapper;
import com.ecommerce.wishlist.model.Wishlist;
import com.ecommerce.wishlist.model.WishlistItem;
import com.ecommerce.wishlist.repository.WishlistItemRepository;
import com.ecommerce.wishlist.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of WishlistService
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final WishlistMapper wishlistMapper;

    @Override
    @Transactional
    public WishlistDto getWishlist(Long userId) {
        log.debug("Fetching wishlist for user: {}", userId);

        // Verify user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Wishlist wishlist = wishlistRepository.findByUserId(userId)
                .orElseGet(() -> {
                    log.info("Wishlist not found for user: {}. Creating new wishlist.", userId);
                    Wishlist newWishlist = new Wishlist();
                    newWishlist.setUser(user);
                    return wishlistRepository.save(newWishlist);
                });

        return wishlistMapper.toDto(wishlist);
    }

    @Override
    public WishlistItemDto addProductToWishlist(Long userId, Long productId) {
        log.debug("Adding product {} to wishlist for user: {}", productId, userId);

        // Verify user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Verify product exists and is active
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        if (!Boolean.TRUE.equals(product.getActive())) {
            throw new ResourceNotFoundException("Product not found with id: " + productId);
        }

        // Get or create wishlist
        Wishlist wishlist = wishlistRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Wishlist newWishlist = new Wishlist();
                    newWishlist.setUser(user);
                    return wishlistRepository.save(newWishlist);
                });

        // Check if product already in wishlist
        if (wishlistItemRepository.existsByWishlistIdAndProductId(wishlist.getId(), productId)) {
            log.debug("Product {} already in wishlist for user: {}", productId, userId);
            throw new com.ecommerce.common.exception.BusinessException(
                    "Product is already in wishlist"
            );
        }

        // Add product to wishlist
        WishlistItem wishlistItem = new WishlistItem();
        wishlistItem.setWishlist(wishlist);
        wishlistItem.setProduct(product);

        WishlistItem savedItem = wishlistItemRepository.save(wishlistItem);
        log.info("Product {} added to wishlist for user: {}", productId, userId);

        return wishlistMapper.toItemDto(savedItem);
    }

    @Override
    public void removeProductFromWishlist(Long userId, Long productId) {
        log.debug("Removing product {} from wishlist for user: {}", productId, userId);

        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        Wishlist wishlist = wishlistRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist not found for user: " + userId));

        // Check if product in wishlist
        if (!wishlistItemRepository.existsByWishlistIdAndProductId(wishlist.getId(), productId)) {
            throw new ResourceNotFoundException("Product not found in wishlist");
        }

        wishlistItemRepository.deleteByWishlistIdAndProductId(wishlist.getId(), productId);
        log.info("Product {} removed from wishlist for user: {}", productId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isProductInWishlist(Long userId, Long productId) {
        log.debug("Checking if product {} is in wishlist for user: {}", productId, userId);

        Wishlist wishlist = wishlistRepository.findByUserId(userId).orElse(null);
        if (wishlist == null) {
            return false;
        }

        return wishlistItemRepository.existsByWishlistIdAndProductId(wishlist.getId(), productId);
    }

    @Override
    public void clearWishlist(Long userId) {
        log.debug("Clearing wishlist for user: {}", userId);

        Wishlist wishlist = wishlistRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist not found for user: " + userId));

        wishlist.getItems().clear();
        wishlistRepository.save(wishlist);
        log.info("Wishlist cleared for user: {}", userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getWishlistItemCount(Long userId) {
        log.debug("Getting wishlist item count for user: {}", userId);

        Wishlist wishlist = wishlistRepository.findByUserId(userId).orElse(null);
        if (wishlist == null) {
            return 0;
        }

        return wishlist.getItems().size();
    }
}
