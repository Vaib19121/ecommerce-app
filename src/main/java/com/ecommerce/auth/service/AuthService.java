package com.ecommerce.auth.service;

import com.ecommerce.auth.dto.JwtResponse;
import com.ecommerce.auth.dto.LoginRequest;
import com.ecommerce.auth.dto.RegisterRequest;
import com.ecommerce.user.dto.UserDto;

public interface AuthService {
    
    UserDto register(RegisterRequest request);
    
    JwtResponse login(LoginRequest request);
    
    boolean validateToken(String token);
}
