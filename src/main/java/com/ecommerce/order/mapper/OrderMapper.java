package com.ecommerce.order.mapper;

import com.ecommerce.order.dto.OrderDto;
import com.ecommerce.order.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "createdDate", target = "date")
    OrderDto toDto(Order order);
}
