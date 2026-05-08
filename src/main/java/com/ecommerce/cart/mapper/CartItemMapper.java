package com.ecommerce.cart.mapper;

import com.ecommerce.cart.dto.CartItemDto;
import com.ecommerce.cart.model.CartItem;
import com.ecommerce.product.model.ProductImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CartItemMapper {
    
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.images", target = "productImage", qualifiedByName = "firstImageUrl")
    @Mapping(source = "product.price", target = "price")
    @Mapping(target = "subtotal", expression = "java(cartItem.getSubtotal())")
    CartItemDto toDto(CartItem cartItem);

    @Named("firstImageUrl")
    default String firstImageUrl(List<ProductImage> images) {
        if (images == null || images.isEmpty()) return null;
        return images.get(0).getUrl();
    }
}
