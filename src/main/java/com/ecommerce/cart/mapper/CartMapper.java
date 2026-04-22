package com.ecommerce.cart.mapper;

import com.ecommerce.cart.dto.CartDto;
import com.ecommerce.cart.dto.CartItemDto;
import com.ecommerce.cart.model.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {CartItemMapper.class})
public interface CartMapper {
    
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "cartItems", target = "items")
    @Mapping(target = "totalPrice", expression = "java(cart.getTotalPrice())")
    @Mapping(target = "totalItems", expression = "java(cart.getCartItems().size())")
    CartDto toDto(Cart cart);
}
