package com.ecommerce.auth.service;

import com.ecommerce.auth.dto.JwtResponse;
import com.ecommerce.auth.dto.LoginRequest;
import com.ecommerce.auth.dto.RegisterRequest;

public interface AuthService {
    
    JwtResponse register(RegisterRequest request);
    
    JwtResponse login(LoginRequest request);
    
    boolean validateToken(String token);
}
