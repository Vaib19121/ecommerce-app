package com.ecommerce.cart.mapper;

import com.ecommerce.cart.dto.CartItemDto;
import com.ecommerce.cart.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CartItemMapper {
    
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.imageUrl", target = "productImage")
    @Mapping(source = "product.price", target = "price")
    @Mapping(target = "subtotal", expression = "java(cartItem.getSubtotal())")
    CartItemDto toDto(CartItem cartItem);
}
