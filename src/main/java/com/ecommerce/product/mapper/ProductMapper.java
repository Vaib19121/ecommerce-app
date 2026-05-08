package com.ecommerce.product.mapper;

import com.ecommerce.product.dto.ProductDto;
import com.ecommerce.product.model.Product;
import com.ecommerce.product.model.ProductImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductMapper {

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "images", target = "images", qualifiedByName = "imagesToUrls")
    ProductDto toDto(Product product);

    @Named("imagesToUrls")
    default List<String> imagesToUrls(List<ProductImage> images) {
        if (images == null) return Collections.emptyList();
        return images.stream()
                .map(ProductImage::getUrl)
                .collect(Collectors.toList());
    }
}
