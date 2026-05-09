package com.ecommerce.payment.service;

import com.ecommerce.payment.dto.PaymentIntentRequest;
import com.ecommerce.payment.dto.PaymentIntentResponse;

public interface PaymentService {

    /**
     * Creates a Stripe PaymentIntent and a PENDING order.
     * Returns the clientSecret for the frontend to confirm payment.
     */
    PaymentIntentResponse createPaymentIntent(Long userId, PaymentIntentRequest request);

    /**
     * Handles incoming Stripe webhook events.
     * Verifies the signature and updates order status accordingly.
     */
    void handleWebhook(String payload, String stripeSignatureHeader);
}
