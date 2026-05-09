package com.ecommerce.payment.controller;

import com.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.common.response.ApiResponse;
import com.ecommerce.payment.dto.PaymentIntentRequest;
import com.ecommerce.payment.dto.PaymentIntentResponse;
import com.ecommerce.payment.service.PaymentService;
import com.ecommerce.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Payments", description = "APIs for Stripe payment processing")
public class PaymentController {

    private final PaymentService paymentService;
    private final UserRepository userRepository;

    @PostMapping("/create-intent")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Create payment intent",
            description = "Initiates a Stripe PaymentIntent for checkout. Returns a clientSecret " +
                    "that the frontend uses with Stripe.js to confirm the payment."
    )
    public ResponseEntity<ApiResponse<PaymentIntentResponse>> createPaymentIntent(
            @Valid @RequestBody PaymentIntentRequest request,
            Authentication authentication) {
        Long userId = getUserId(authentication);
        PaymentIntentResponse response = paymentService.createPaymentIntent(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Payment intent created", response));
    }

    @PostMapping("/webhook")
    @Operation(
            summary = "Stripe webhook",
            description = "Receives Stripe webhook events. The endpoint is public but " +
                    "protected by Stripe signature verification."
    )
    public ResponseEntity<Void> handleWebhook(
            @RequestBody byte[] rawPayload,
            @RequestHeader("Stripe-Signature") String stripeSignature) {
        String payload = new String(rawPayload, StandardCharsets.UTF_8);
        paymentService.handleWebhook(payload, stripeSignature);
        return ResponseEntity.ok().build();
    }

    private Long getUserId(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"))
                .getId();
    }
}
