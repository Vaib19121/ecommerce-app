package com.ecommerce.order.service;

import com.ecommerce.common.exception.BusinessException;
import com.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.common.exception.UnauthorizedException;
import com.ecommerce.order.dto.CreateOrderRequest;
import com.ecommerce.order.dto.OrderDto;
import com.ecommerce.order.dto.UpdateOrderStatusRequest;
import com.ecommerce.order.mapper.OrderMapper;
import com.ecommerce.order.model.Order;
import com.ecommerce.order.model.OrderStatus;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.product.model.Product;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.user.model.User;
import com.ecommerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderDto createOrder(Long userId, CreateOrderRequest request, String paymentIntentId) {
        log.info("Creating order for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // 1. Fetch product and validate it
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + request.productId()));

        if (!product.getActive()) {
            throw new BusinessException("Product is not available");
        }

        if (product.getStockQuantity() < request.quantity()) {
            throw new BusinessException("Insufficient stock. Available: " + product.getStockQuantity());
        }

        // 2. Derive image from the first product image (if any)
        String imageUrl = product.getImages().isEmpty()
                ? null
                : product.getImages().get(0).getUrl();

        // 3. Calculate total price = product price × quantity
        java.math.BigDecimal totalPrice = product.getPrice()
                .multiply(java.math.BigDecimal.valueOf(request.quantity()));

        // 4. Build order — starts as PENDING until Stripe confirms payment
        Order order = Order.builder()
                .product(product.getName())
                .image(imageUrl)
                .price(totalPrice)
                .status(OrderStatus.PENDING)
                .paymentIntentId(paymentIntentId)
                .user(user)
                .build();

        Order saved = orderRepository.save(order);
        log.info("Order created with id: {}, status: PENDING", saved.getId());
        return orderMapper.toDto(saved);
    }

    @Override
    public OrderDto getOrderById(Long orderId, Long userId) {
        log.debug("Fetching order {} for user {}", orderId, userId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (!order.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to view this order");
        }

        return orderMapper.toDto(order);
    }

    @Override
    public Page<OrderDto> getOrdersByUser(Long userId, Pageable pageable) {
        log.debug("Fetching orders for user: {}", userId);
        return orderRepository.findByUserId(userId, pageable).map(orderMapper::toDto);
    }

    @Override
    public Page<OrderDto> getOrdersByUserAndStatus(Long userId, OrderStatus status, Pageable pageable) {
        log.debug("Fetching orders for user {} with status {}", userId, status);
        return orderRepository.findByUserIdAndStatus(userId, status, pageable).map(orderMapper::toDto);
    }

    @Override
    @Transactional
    public OrderDto updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
        log.info("Updating status for order: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        order.setStatus(request.status());
        Order updated = orderRepository.save(order);

        log.info("Order {} status updated to {}", orderId, request.status());
        return orderMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void deleteOrder(Long orderId, Long userId) {
        log.info("Deleting order {} for user {}", orderId, userId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (!order.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to delete this order");
        }

        orderRepository.delete(order);
        log.info("Order {} deleted", orderId);
    }
}
