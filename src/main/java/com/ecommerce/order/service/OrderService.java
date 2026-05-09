package com.ecommerce.order.service;

import com.ecommerce.order.dto.CreateOrderRequest;
import com.ecommerce.order.dto.OrderDto;
import com.ecommerce.order.dto.UpdateOrderStatusRequest;
import com.ecommerce.order.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    /**
     * Creates a PENDING order linked to a Stripe PaymentIntent.
     * Called by PaymentService after a PaymentIntent is created.
     */
    OrderDto createOrder(Long userId, CreateOrderRequest request, String paymentIntentId);

    OrderDto getOrderById(Long orderId, Long userId);

    Page<OrderDto> getOrdersByUser(Long userId, Pageable pageable);

    Page<OrderDto> getOrdersByUserAndStatus(Long userId, OrderStatus status, Pageable pageable);

    OrderDto updateOrderStatus(Long orderId, UpdateOrderStatusRequest request);

    void deleteOrder(Long orderId, Long userId);
}
