package com.ecommerce.payment.service;

import com.ecommerce.cart.repository.CartItemRepository;
import com.ecommerce.cart.repository.CartRepository;
import com.ecommerce.common.exception.BusinessException;
import com.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.order.dto.CreateOrderRequest;
import com.ecommerce.order.model.Order;
import com.ecommerce.order.model.OrderStatus;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.order.service.OrderService;
import com.ecommerce.payment.dto.PaymentIntentRequest;
import com.ecommerce.payment.dto.PaymentIntentResponse;
import com.ecommerce.product.model.Product;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.user.model.User;
import com.ecommerce.user.repository.UserRepository;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.ApiResource;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Value("${stripe.secret-key}")
    private String stripeSecretKey;

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    @Override
    @Transactional
    public PaymentIntentResponse createPaymentIntent(Long userId, PaymentIntentRequest request) {
        // 1. Validate user and product
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + request.productId()));

        if (!product.getActive()) {
            throw new BusinessException("Product is not available");
        }

        if (product.getStockQuantity() < request.quantity()) {
            throw new BusinessException("Insufficient stock. Available: " + product.getStockQuantity());
        }

        // 2. Calculate amount in smallest currency unit (cents)
        BigDecimal totalPrice = product.getPrice()
                .multiply(BigDecimal.valueOf(request.quantity()));
        long amountInCents = totalPrice.multiply(BigDecimal.valueOf(100)).longValue();

        // 3. Create Stripe PaymentIntent
        PaymentIntent paymentIntent;
        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountInCents)
                    .setCurrency("usd")
                    .setDescription("Purchase of " + product.getName() + " (qty: " + request.quantity() + ")")
                    .setShipping(
                            PaymentIntentCreateParams.Shipping.builder()
                                    .setName(user.getFullName())
                                    .setAddress(
                                            PaymentIntentCreateParams.Shipping.Address.builder()
                                                    .setCountry("IN")
                                                    .build()
                                    )
                                    .build()
                    )
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .build()
                    )
                    .putMetadata("userId", String.valueOf(userId))
                    .putMetadata("productId", String.valueOf(request.productId()))
                    .putMetadata("quantity", String.valueOf(request.quantity()))
                    .build();

            paymentIntent = PaymentIntent.create(params);
        } catch (StripeException e) {
            log.error("Stripe PaymentIntent creation failed: {}", e.getMessage());
            throw new BusinessException("Payment initialization failed. Please try again.");
        }

        // 4. Create a PENDING order linked to the PaymentIntent
        CreateOrderRequest orderRequest = new CreateOrderRequest(request.productId(), request.quantity());
        var order = orderService.createOrder(userId, orderRequest, paymentIntent.getId());

        log.info("PaymentIntent created: {} for user: {}", paymentIntent.getId(), userId);

        // 5. Return clientSecret to the frontend
        return new PaymentIntentResponse(
                paymentIntent.getClientSecret(),
                paymentIntent.getId(),
                order.id(),
                totalPrice,
                "usd"
        );
    }

    @Override
    @Transactional
    public void handleWebhook(String payload, String stripeSignatureHeader) {
        // 1. Verify the webhook signature (prevents forged requests)
        Event event;
        log.info("Webhook {}",payload);
        try {
            event = Webhook.constructEvent(payload, stripeSignatureHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            log.warn("Invalid Stripe webhook signature");
            throw new BusinessException("Invalid webhook signature");
        }

        log.info("Received Stripe webhook event: {}", event.getType());

        // 2. Handle relevant events
        switch (event.getType()) {
            case "payment_intent.succeeded" -> {
                PaymentIntent paymentIntent = ApiResource.GSON.fromJson(
                        event.getDataObjectDeserializer().getRawJson(), PaymentIntent.class);
                updateOrderStatus(paymentIntent.getId(), OrderStatus.IN_TRANSIT);
                removeProductFromCart(paymentIntent);
                log.info("Payment succeeded for PaymentIntent: {}", paymentIntent.getId());
            }
            case "payment_intent.payment_failed" -> {
                PaymentIntent paymentIntent = ApiResource.GSON.fromJson(
                        event.getDataObjectDeserializer().getRawJson(), PaymentIntent.class);
                updateOrderStatus(paymentIntent.getId(), OrderStatus.CANCELLED);
                removeProductFromCart(paymentIntent);
                log.warn("Payment failed for PaymentIntent: {}", paymentIntent.getId());
            }
            default -> log.debug("Unhandled Stripe event type: {}", event.getType());
        }
    }

    private void updateOrderStatus(String paymentIntentId, OrderStatus status) {
        Order order = orderRepository.findByPaymentIntentId(paymentIntentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found for PaymentIntent: " + paymentIntentId));
        order.setStatus(status);
        orderRepository.save(order);
        log.info("Order {} status updated to {}", order.getId(), status);
    }

    private void removeProductFromCart(PaymentIntent paymentIntent) {
        try {
            String userIdStr = paymentIntent.getMetadata().get("userId");
            String productIdStr = paymentIntent.getMetadata().get("productId");
            if (userIdStr == null || productIdStr == null) return;

            Long userId = Long.parseLong(userIdStr);
            Long productId = Long.parseLong(productIdStr);

            cartRepository.findByUserId(userId).ifPresent(cart ->
                    cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                            .ifPresent(item -> {
                                cart.removeItem(item);
                                cartItemRepository.delete(item);
                                log.info("Removed product {} from cart for user {}", productId, userId);
                            })
            );
        } catch (Exception e) {
            log.warn("Could not remove product from cart: {}", e.getMessage());
        }
    }
}

