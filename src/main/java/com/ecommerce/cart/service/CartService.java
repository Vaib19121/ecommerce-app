package com.ecommerce.cart.service;

import com.ecommerce.cart.dto.AddToCartRequest;
import com.ecommerce.cart.dto.CartDto;
import com.ecommerce.cart.dto.UpdateCartItemRequest;

public interface CartService {
    
    CartDto getCartByUserId(Long userId);
    
    CartDto addItemToCart(Long userId, AddToCartRequest request);
    
    CartDto updateCartItemQuantity(Long userId, Long itemId, UpdateCartItemRequest request);
    
    CartDto removeItemFromCart(Long userId, Long itemId);
    
    void clearCart(Long userId);
}
