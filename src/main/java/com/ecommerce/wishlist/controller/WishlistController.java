package com.ecommerce.wishlist.controller;

import com.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.common.response.ApiResponse;
import com.ecommerce.user.repository.UserRepository;
import com.ecommerce.wishlist.dto.WishlistDto;
import com.ecommerce.wishlist.dto.WishlistItemDto;
import com.ecommerce.wishlist.dto.WishlistProductRequest;
import com.ecommerce.wishlist.service.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for Wishlist operations
 */
@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
@Tag(name = "Wishlist Management", description = "APIs for managing user wishlist")
public class WishlistController {

    private final WishlistService wishlistService;
    private final UserRepository userRepository;

    /**
     * Get user's wishlist
     */
    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Get user's wishlist", description = "Retrieve the authenticated user's wishlist with all items")
    public ResponseEntity<ApiResponse<WishlistDto>> getWishlist(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        WishlistDto wishlist = wishlistService.getWishlist(userId);
        return ResponseEntity.ok(ApiResponse.success("Wishlist retrieved successfully", wishlist));
    }

    /**
     * Add product to wishlist
     */
    @PostMapping("/items")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Add product to wishlist", description = "Add a product to the user's wishlist")
    public ResponseEntity<ApiResponse<WishlistItemDto>> addProductToWishlist(
            Authentication authentication,
            @Valid @RequestBody WishlistProductRequest request) {
        Long userId = getUserIdFromAuthentication(authentication);
        WishlistItemDto item = wishlistService.addProductToWishlist(userId, request.getProductId());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product added to wishlist successfully", item));
    }

    /**
     * Remove product from wishlist
     */
    @DeleteMapping("/items/{productId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Remove product from wishlist", description = "Remove a product from the user's wishlist")
    public ResponseEntity<ApiResponse<Void>> removeProductFromWishlist(
            Authentication authentication,
            @PathVariable Long productId) {
        Long userId = getUserIdFromAuthentication(authentication);
        wishlistService.removeProductFromWishlist(userId, productId);
        return ResponseEntity.ok(ApiResponse.success("Product removed from wishlist successfully", null));
    }

    /**
     * Check if product is in wishlist
     */
    @GetMapping("/items/{productId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Check if product is in wishlist", description = "Check if a specific product is in the user's wishlist")
    public ResponseEntity<ApiResponse<Map<String, Object>>> isProductInWishlist(
            Authentication authentication,
            @PathVariable Long productId) {
        Long userId = getUserIdFromAuthentication(authentication);
        boolean inWishlist = wishlistService.isProductInWishlist(userId, productId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("productId", productId);
        response.put("inWishlist", inWishlist);
        
        return ResponseEntity.ok(ApiResponse.success("Product wishlist status retrieved", response));
    }

    /**
     * Get wishlist item count
     */
    @GetMapping("/count")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Get wishlist item count", description = "Get the number of items in the user's wishlist")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getWishlistItemCount(
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        Integer count = wishlistService.getWishlistItemCount(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("count", count);
        
        return ResponseEntity.ok(ApiResponse.success("Wishlist item count retrieved", response));
    }

    /**
     * Clear entire wishlist
     */
    @DeleteMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Clear wishlist", description = "Remove all products from the user's wishlist")
    public ResponseEntity<ApiResponse<Void>> clearWishlist(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        wishlistService.clearWishlist(userId);
        return ResponseEntity.ok(ApiResponse.success("Wishlist cleared successfully", null));
    }

    /**
     * Extract user ID from authentication token
     */
    private Long getUserIdFromAuthentication(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        String emailFromAuth = null;

        if (principal instanceof UserDetails) {
            emailFromAuth = ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            emailFromAuth = (String) principal;
        }

        if (emailFromAuth == null) {
            throw new ResourceNotFoundException("Cannot extract user information from authentication");
        }

        final String email = emailFromAuth;
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email))
                .getId();
    }
}

