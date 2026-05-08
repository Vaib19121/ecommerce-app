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
    private String first_name;
    private String last_name;

    public JwtResponse(String token, String refreshToken, String email, Role role,String first_name, String last_name) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.email = email;
        this.role = role;
        this.first_name = first_name;
        this.last_name = last_name;
    }

}
