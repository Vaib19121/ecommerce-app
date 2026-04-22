package com.ecommerce.user.mapper;

import com.ecommerce.user.dto.UserDto;
import com.ecommerce.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    
    @Mapping(source = "fullName", target = "fullName")
    UserDto toDto(User user);
}
