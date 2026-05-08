package com.ecommerce.auth.dto;

import com.ecommerce.user.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    
    private String token;
    private String refreshToken;
    private String type = "Bearer";
    private String email;
    private Role role;

    public JwtResponse(String token, String refreshToken, String email, Role role) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.email = email;
        this.role = role;
    }
}
