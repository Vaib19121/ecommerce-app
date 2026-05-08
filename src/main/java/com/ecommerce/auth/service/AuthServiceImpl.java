package com.ecommerce.auth.service;

import com.ecommerce.auth.dto.JwtResponse;
import com.ecommerce.auth.dto.LoginRequest;
import com.ecommerce.auth.dto.RegisterRequest;
import com.ecommerce.auth.jwt.JwtTokenProvider;
import com.ecommerce.cart.model.Cart;
import com.ecommerce.common.exception.BusinessException;
import com.ecommerce.user.model.Role;
import com.ecommerce.user.model.User;
import com.ecommerce.user.repository.UserRepository;
import com.ecommerce.wishlist.model.Wishlist;
import com.ecommerce.wishlist.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final WishlistRepository wishlistRepository;

    @Override
    public JwtResponse register(RegisterRequest request) {
        log.debug("Registering new user with email: {}", request.getEmail());
        
        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already in use: " + request.getEmail());
        }

        // Create new user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole(Role.CUSTOMER); // Default role

        // Create empty cart for user
        Cart cart = new Cart();
        cart.setUser(user);
        user.setCart(cart);

        // Create empty wishlist for user
        Wishlist wishlist = new Wishlist();
        wishlist.setUser(user);
        user.setWishlist(wishlist);

        // Save user
        User savedUser = userRepository.save(user);
        log.info("User registered successfully with cart and wishlist: {}", savedUser.getEmail());

        // Generate tokens
        String accessToken = jwtTokenProvider.generateTokenFromEmail(savedUser.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(savedUser.getEmail());

        return new JwtResponse(accessToken, refreshToken, savedUser.getEmail(), savedUser.getRole(),savedUser.getFirstName(), savedUser.getLastName());
    }

    @Override
    public JwtResponse login(LoginRequest request) {
        log.debug("User login attempt: {}", request.getEmail());

        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate JWT token
        String jwt = jwtTokenProvider.generateToken(authentication);

        // Get user details
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("User not found"));

        log.info("User logged in successfully: {}", user.getEmail());

        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());
        return new JwtResponse(jwt, refreshToken, user.getEmail(), user.getRole(),user.getFirstName(), user.getLastName());
    }

    @Override
    public boolean validateToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }
}
