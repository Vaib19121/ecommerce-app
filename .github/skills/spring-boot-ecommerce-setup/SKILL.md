---
name: spring-boot-ecommerce-setup
description: "Use when: setting up, understanding, or troubleshooting the Spring Boot ecommerce application; explaining project structure; running the application; adding new features; understanding authentication flow; working with the database; or general questions about this Spring Boot project architecture"
---

# Spring Boot E-Commerce Application Setup & Reference

## Purpose

This skill provides comprehensive guidance for working with this Spring Boot e-commerce REST API, including setup instructions, architectural patterns, and development workflows.

## When to Use This Skill

- Setting up the project for the first time
- Understanding the overall architecture and design patterns
- Adding new feature modules (products, orders, payments, etc.)
- Troubleshooting common issues
- Understanding authentication and authorization flow
- Database setup and migrations
- Running tests or building the application
- API documentation and testing

## Quick Start Guide

### Prerequisites Check
```bash
# Verify Java 17+
java -version

# Verify Maven 3.6+
mvn -version

# PostgreSQL 14+ (for production only)
psql --version
```

### Initial Setup

1. **Clone and Navigate**
   ```bash
   cd /Users/vaibhavmehar/Desktop/All_Docs_Projects/Java_Workspace/SpringBoot/ecommerce-app
   ```

2. **Build the Project**
   ```bash
   ./mvnw clean install
   ```

3. **Run Development Mode** (uses H2 in-memory database)
   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
   ```

4. **Access the Application**
   - API Base: http://localhost:8080/api
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - H2 Console: http://localhost:8080/h2-console (dev only)
     - JDBC URL: `jdbc:h2:mem:ecommerce`
     - Username: `sa`
     - Password: (empty)

### Default Test Credentials (Dev Mode)

| Email | Password | Role |
|-------|----------|------|
| admin@ecommerce.com | admin123 | ADMIN |
| customer@ecommerce.com | customer123 | CUSTOMER |

## Project Architecture

### Module Structure

Each feature follows a consistent 6-layer pattern:

```
feature-name/
├── controller/      # REST endpoints
│   └── FeatureController.java
├── service/        # Business logic
│   ├── FeatureService.java (interface)
│   └── FeatureServiceImpl.java
├── repository/     # Data access
│   └── FeatureRepository.java
├── model/          # JPA entities
│   └── Feature.java
├── dto/            # Data transfer objects
│   ├── FeatureDto.java
│   ├── FeatureCreateRequest.java
│   └── FeatureUpdateRequest.java
└── mapper/         # MapStruct mappers
    └── FeatureMapper.java
```

### Existing Modules

1. **auth/** - JWT authentication, login, registration
2. **user/** - User management, profiles
3. **product/** - Product catalog CRUD
4. **category/** - Product categorization
5. **cart/** - Shopping cart operations
6. **common/** - Shared components (config, exceptions, responses)

## Key Design Patterns

### 1. Service Interface Pattern
```java
// Always create an interface
public interface ProductService {
    ProductDto createProduct(ProductCreateRequest request);
    ProductDto getProductById(Long id);
    // ... other methods
}

// Then implementation
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {
    // Implementation
}
```

### 2. Standardized API Responses
```java
// All endpoints return ApiResponse<T>
return ResponseEntity.ok(
    ApiResponse.success("Product retrieved successfully", product)
);

// For errors, GlobalExceptionHandler handles it
throw new ResourceNotFoundException("Product not found with id: " + id);
```

### 3. MapStruct for DTO Mapping
```java
@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductDto toDto(Product product);
    Product toEntity(ProductCreateRequest request);
    void updateEntityFromDto(ProductUpdateRequest request, @MappingTarget Product product);
}
```

### 4. JWT Security Flow
1. Client sends credentials to `/api/auth/login`
2. Server validates and returns JWT token
3. Client includes token in `Authorization: Bearer {token}` header
4. `JwtAuthenticationFilter` validates token on each request
5. `SecurityContext` populated with user details

## Common Development Tasks

### Adding a New Entity/Feature

**Step-by-step workflow:**

1. **Create Entity**
   ```java
   @Entity
   @Table(name = "orders")
   @EntityListeners(AuditingEntityListener.class)
   public class Order {
       @Id
       @GeneratedValue(strategy = GenerationType.IDENTITY)
       private Long id;
       
       @CreatedDate
       private LocalDateTime createdDate;
       
       @LastModifiedDate
       private LocalDateTime lastModifiedDate;
       
       @Version
       private Long version;
       
       // Fields, relationships, getters, setters
   }
   ```

2. **Create Repository**
   ```java
   public interface OrderRepository extends JpaRepository<Order, Long> {
       List<Order> findByUserId(Long userId);
   }
   ```

3. **Create DTOs**
   ```java
   public record OrderDto(Long id, /* fields */) {}
   public record OrderCreateRequest(@NotNull /* fields */) {}
   public record OrderUpdateRequest(/* fields */) {}
   ```

4. **Create Mapper**
   ```java
   @Mapper(componentModel = "spring")
   public interface OrderMapper {
       OrderDto toDto(Order order);
       Order toEntity(OrderCreateRequest request);
   }
   ```

5. **Create Service Interface & Implementation**
   ```java
   public interface OrderService {
       OrderDto createOrder(OrderCreateRequest request);
   }
   
   @Service
   @RequiredArgsConstructor
   @Transactional(readOnly = true)
   public class OrderServiceImpl implements OrderService {
       private final OrderRepository orderRepository;
       private final OrderMapper orderMapper;
       
       @Override
       @Transactional
       public OrderDto createOrder(OrderCreateRequest request) {
           // Business logic
       }
   }
   ```

6. **Create Controller**
   ```java
   @RestController
   @RequestMapping("/api/orders")
   @RequiredArgsConstructor
   @Tag(name = "Order Management")
   public class OrderController {
       private final OrderService orderService;
       
       @PostMapping
       @PreAuthorize("hasRole('CUSTOMER')")
       @Operation(summary = "Create order")
       public ResponseEntity<ApiResponse<OrderDto>> createOrder(
               @Valid @RequestBody OrderCreateRequest request) {
           OrderDto order = orderService.createOrder(request);
           return ResponseEntity.status(HttpStatus.CREATED)
                   .body(ApiResponse.success("Order created", order));
       }
   }
   ```

### Running Tests
```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=ProductServiceTest

# Run with coverage
./mvnw test jacoco:report
```

### Building for Production
```bash
# Create JAR
./mvnw clean package -DskipTests

# Run JAR
java -jar target/ecommerce-app-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

## Configuration Management

### Environment Variables (Production)
```bash
export JWT_SECRET=your-256-bit-secret-key
export DB_URL=jdbc:postgresql://localhost:5432/ecommerce_db
export DB_USERNAME=your_username
export DB_PASSWORD=your_password
```

### Profile-Specific Configuration

- **application.yml** - Common configuration
- **application-dev.yml** - Development (H2, sample data)
- **application-prod.yml** - Production (PostgreSQL)

Activate profile:
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

## Troubleshooting Guide

### Common Issues

1. **Build Failures**
   ```bash
   # Clear Maven cache
   ./mvnw dependency:purge-local-repository
   ./mvnw clean install
   ```

2. **Port Already in Use**
   ```bash
   # Kill process on port 8080
   lsof -ti:8080 | xargs kill -9
   ```

3. **Database Connection Issues (Production)**
   - Verify PostgreSQL is running
   - Check credentials in application-prod.yml
   - Ensure database exists: `createdb ecommerce_db`

4. **JWT Token Issues**
   - Verify JWT_SECRET is set and at least 256 bits
   - Check token expiration (default: 24 hours)
   - Ensure token format: `Bearer {token}`

5. **MapStruct Not Generating Mappers**
   ```bash
   # Force recompilation
   ./mvnw clean compile
   
   # Check generated sources
   ls target/generated-sources/annotations/com/ecommerce/*/mapper/
   ```

## API Testing

### Using Swagger UI
1. Navigate to http://localhost:8080/swagger-ui.html
2. Authenticate: POST `/api/auth/login`
3. Copy JWT token from response
4. Click "Authorize" button, enter: `Bearer {token}`
5. Test endpoints

### Using cURL
```bash
# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@ecommerce.com","password":"admin123"}'

# Use token
TOKEN="your-jwt-token"
curl -X GET http://localhost:8080/api/products \
  -H "Authorization: Bearer $TOKEN"
```

## Database Schema

### Key Tables
- **users** - User accounts (customer/admin)
- **products** - Product catalog
- **categories** - Product categories
- **carts** - Shopping carts (one per user)
- **cart_items** - Items in cart (junction table)

### Relationships
- User ↔ Cart (one-to-one)
- Cart ↔ CartItem (one-to-many)
- CartItem ↔ Product (many-to-one)
- Product ↔ Category (many-to-one)

## Best Practices

### Security
- ✅ Never commit JWT_SECRET to version control
- ✅ Use environment variables for sensitive data
- ✅ Always validate user input with `@Valid`
- ✅ Apply `@PreAuthorize` for protected endpoints
- ✅ Use BCrypt for password hashing (automatic)

### Performance
- ✅ Use pagination for list endpoints
- ✅ Apply `@Transactional(readOnly = true)` for queries
- ✅ Use `@Version` for optimistic locking
- ✅ Index foreign keys and frequently queried fields

### Code Quality
- ✅ Follow the 6-layer architecture pattern
- ✅ Use MapStruct instead of manual mapping
- ✅ Write service layer unit tests
- ✅ Document all endpoints with Swagger annotations
- ✅ Use custom exceptions for business logic errors

## Reference Files

- **README.md** - Project overview and quick start
- **pom.xml** - Maven dependencies and build configuration
- **application.yml** - Common configuration
- **SecurityConfig.java** - Security configuration
- **GlobalExceptionHandler.java** - Error handling patterns

## Next Steps

After setup:
1. Explore the codebase using Swagger UI
2. Review existing feature modules as examples
3. Run tests to verify everything works
4. Try adding a simple new feature (e.g., reviews, wishlist)
5. Review security configuration for authentication flow

## Getting Help

When asking for help, provide:
- Which profile you're running (dev/prod)
- Relevant error messages or stack traces
- What you were trying to accomplish
- Steps you've already tried
