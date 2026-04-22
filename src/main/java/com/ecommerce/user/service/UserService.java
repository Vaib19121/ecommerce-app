package com.ecommerce.user.service;

import com.ecommerce.user.dto.UserDto;
import com.ecommerce.user.dto.UserUpdateRequest;

public interface UserService {
    
    UserDto getUserById(Long id);
    
    UserDto getUserByEmail(String email);
    
    UserDto updateUser(Long id, UserUpdateRequest request);
    
    void deleteUser(Long id);
}
