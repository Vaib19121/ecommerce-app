package com.ecommerce.wishlist.mapper;

import com.ecommerce.product.model.Product;
import com.ecommerce.wishlist.dto.WishlistDto;
import com.ecommerce.wishlist.dto.WishlistItemDto;
import com.ecommerce.wishlist.model.Wishlist;
import com.ecommerce.wishlist.model.WishlistItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for Wishlist and WishlistItem DTOs
 */
@Mapper(componentModel = "spring")
public interface WishlistMapper {

    @Mapping(target = "totalItems", expression = "java(wishlist.getItems().size())")
    WishlistDto toDto(Wishlist wishlist);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.brand", target = "brand")
    @Mapping(source = "product.price", target = "price")
    @Mapping(source = "product.originalPrice", target = "originalPrice")
    @Mapping(source = "product.discountPercentage", target = "discountPercentage")
    @Mapping(source = "product.images", target = "imageUrl", qualifiedByName = "getFirstImageUrl")
    @Mapping(expression = "java(wishlistItem.getProduct().getStockQuantity() != null && wishlistItem.getProduct().getStockQuantity() > 0)", target = "inStock")
    @Mapping(source = "createdDate", target = "addedDate")
    WishlistItemDto toItemDto(WishlistItem wishlistItem);

    @org.mapstruct.Named("getFirstImageUrl")
    default String getFirstImageUrl(java.util.List<com.ecommerce.product.model.ProductImage> images) {
        if (images != null && !images.isEmpty()) {
            return images.get(0).getUrl();
        }
        return null;
    }
}
