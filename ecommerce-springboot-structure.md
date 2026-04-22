# E-Commerce Spring Boot — Feature-Based Folder Structure

```
ecommerce/
├── src/
│   └── main/
│       ├── java/com/ecommerce/
│       │   │
│       │   ├── EcommerceApplication.java
│       │   │
│       │   ├── common/                          # Shared utilities
│       │   │   ├── exception/
│       │   │   │   ├── GlobalExceptionHandler.java
│       │   │   │   ├── ResourceNotFoundException.java
│       │   │   │   └── BusinessException.java
│       │   │   ├── response/
│       │   │   │   └── ApiResponse.java
│       │   │   ├── config/
│       │   │   │   ├── SecurityConfig.java
│       │   │   │   ├── SwaggerConfig.java
│       │   │   │   └── CorsConfig.java
│       │   │   └── utils/
│       │   │       └── DateUtils.java
│       │   │
│       │   ├── auth/                            # Authentication & Authorization
│       │   │   ├── controller/
│       │   │   │   └── AuthController.java
│       │   │   ├── service/
│       │   │   │   └── AuthService.java
│       │   │   ├── dto/
│       │   │   │   ├── LoginRequest.java
│       │   │   │   ├── RegisterRequest.java
│       │   │   │   └── JwtResponse.java
│       │   │   └── jwt/
│       │   │       ├── JwtTokenProvider.java
│       │   │       └── JwtAuthFilter.java
│       │   │
│       │   ├── user/                            # User Management
│       │   │   ├── controller/
│       │   │   │   └── UserController.java
│       │   │   ├── service/
│       │   │   │   ├── UserService.java
│       │   │   │   └── UserServiceImpl.java
│       │   │   ├── repository/
│       │   │   │   └── UserRepository.java
│       │   │   ├── model/
│       │   │   │   └── User.java
│       │   │   └── dto/
│       │   │       ├── UserDto.java
│       │   │       └── UserUpdateRequest.java
│       │   │
│       │   ├── product/                         # Product Management
│       │   │   ├── controller/
│       │   │   │   └── ProductController.java
│       │   │   ├── service/
│       │   │   │   ├── ProductService.java
│       │   │   │   └── ProductServiceImpl.java
│       │   │   ├── repository/
│       │   │   │   └── ProductRepository.java
│       │   │   ├── model/
│       │   │   │   └── Product.java
│       │   │   └── dto/
│       │   │       ├── ProductDto.java
│       │   │       ├── ProductCreateRequest.java
│       │   │       └── ProductUpdateRequest.java
│       │   │
│       │   ├── category/                        # Category Management
│       │   │   ├── controller/
│       │   │   │   └── CategoryController.java
│       │   │   ├── service/
│       │   │   │   └── CategoryServiceImpl.java
│       │   │   ├── repository/
│       │   │   │   └── CategoryRepository.java
│       │   │   ├── model/
│       │   │   │   └── Category.java
│       │   │   └── dto/
│       │   │       └── CategoryDto.java
│       │   │
│       │   ├── cart/                            # Shopping Cart
│       │   │   ├── controller/
│       │   │   │   └── CartController.java
│       │   │   ├── service/
│       │   │   │   └── CartServiceImpl.java
│       │   │   ├── repository/
│       │   │   │   └── CartRepository.java
│       │   │   ├── model/
│       │   │   │   ├── Cart.java
│       │   │   │   └── CartItem.java
│       │   │   └── dto/
│       │   │       ├── CartDto.java
│       │   │       └── CartItemRequest.java
│       │   │
│       │   ├── order/                           # Order Management
│       │   │   ├── controller/
│       │   │   │   └── OrderController.java
│       │   │   ├── service/
│       │   │   │   └── OrderServiceImpl.java
│       │   │   ├── repository/
│       │   │   │   ├── OrderRepository.java
│       │   │   │   └── OrderItemRepository.java
│       │   │   ├── model/
│       │   │   │   ├── Order.java
│       │   │   │   ├── OrderItem.java
│       │   │   │   └── OrderStatus.java        # Enum
│       │   │   └── dto/
│       │   │       ├── OrderDto.java
│       │   │       └── OrderRequest.java
│       │   │
│       │   ├── payment/                         # Payment
│       │   │   ├── controller/
│       │   │   │   └── PaymentController.java
│       │   │   ├── service/
│       │   │   │   └── PaymentServiceImpl.java
│       │   │   ├── model/
│       │   │   │   ├── Payment.java
│       │   │   │   └── PaymentStatus.java      # Enum
│       │   │   └── dto/
│       │   │       └── PaymentRequest.java
│       │   │
│       │   ├── review/                          # Product Reviews
│       │   │   ├── controller/
│       │   │   │   └── ReviewController.java
│       │   │   ├── service/
│       │   │   │   └── ReviewServiceImpl.java
│       │   │   ├── repository/
│       │   │   │   └── ReviewRepository.java
│       │   │   ├── model/
│       │   │   │   └── Review.java
│       │   │   └── dto/
│       │   │       └── ReviewDto.java
│       │   │
│       │   └── admin/                           # Admin Dashboard
│       │       ├── controller/
│       │       │   └── AdminController.java
│       │       └── service/
│       │           └── AdminServiceImpl.java
│       │
│       └── resources/
│           ├── application.yml
│           ├── application-dev.yml
│           └── application-prod.yml
│
├── pom.xml
└── README.md
```

---

## Key Principles

### Each feature is self-contained
`product/`, `order/`, `cart/` etc. each have their own `controller`, `service`, `repository`, `model`, and `dto` folders. No jumping between global folders.

### `common/` for shared code
Exception handling, API response wrapper, security config, and utilities all live here.

### DTOs are per-feature
Request/response objects live right next to the feature they belong to — easy to find and modify.

### Enums live in the model folder
Feature-specific enums (e.g., `OrderStatus`, `PaymentStatus`) stay inside their own feature's `model/` folder.

---

## Features Overview

| Feature | Description |
|---|---|
| `auth` | JWT-based login, registration, token filter |
| `user` | User profile management |
| `product` | Product CRUD, search, pagination |
| `category` | Product categories |
| `cart` | Shopping cart with line items |
| `order` | Order placement and tracking |
| `payment` | Payment processing and status |
| `review` | Product ratings and reviews |
| `admin` | Admin-only dashboard APIs |
| `common` | Shared configs, exceptions, utilities |
