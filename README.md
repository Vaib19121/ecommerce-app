# E-Commerce Application

A modern e-commerce REST API built with Spring Boot, featuring JWT authentication, shopping cart management, product catalog, and more.

## Features

- **User Authentication & Authorization**
  - JWT-based authentication
  - Role-based access control (ADMIN, CUSTOMER)
  - Secure password encryption with BCrypt

- **Product Management**
  - CRUD operations for products
  - Category-based organization
  - Search and filtering
  - Pagination and sorting
  - Soft delete with active/inactive status
  - Optimistic locking for concurrent updates

- **Shopping Cart**
  - Add/remove/update items
  - Automatic cart creation on registration
  - Stock validation
  - Real-time price calculation

- **Category Management**
  - CRUD operations (Admin only)
  - Product count tracking
  - Search functionality

## Tech Stack

- **Framework**: Spring Boot 4.0.5
- **Database**: PostgreSQL (production), H2 (development)
- **Security**: Spring Security + JWT (jjwt 0.12.5)
- **ORM**: Spring Data JPA + Hibernate
- **DTO Mapping**: MapStruct 1.6.3
- **API Documentation**: Springdoc OpenAPI 2.8.6 (Swagger UI)
- **Build Tool**: Maven
- **Java Version**: 17

## Prerequisites

- JDK 17 or higher
- Maven 3.6+
- PostgreSQL 14+ (for production)

## Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd ecommerce-app
```

### 2. Configure Database

For **development** (H2 in-memory database is configured by default):
```bash
# No additional configuration needed
```

For **production**, update `src/main/resources/application-prod.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/ecommerce_db
    username: your_username
    password: your_password
```

### 3. Build the Application

```bash
./mvnw clean install
```

### 4. Run the Application

**Development mode** (with sample data):
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

**Production mode**:
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

The application will start on `http://localhost:8080`

## Default Users (Development Mode)

| Email | Password | Role |
|-------|----------|------|
| admin@ecommerce.com | admin123 | ADMIN |
| customer@ecommerce.com | customer123 | CUSTOMER |

## API Documentation

Once the application is running, access Swagger UI at:
```
http://localhost:8080/swagger-ui.html
```

OpenAPI specification:
```
http://localhost:8080/v3/api-docs
```

## API Endpoints

### Authentication

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | `/api/auth/register` | Register new user | Public |
| POST | `/api/auth/login` | Login and get JWT token | Public |

### Users

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | `/api/users/{id}` | Get user by ID | Authenticated |
| PUT | `/api/users/{id}` | Update user | User/Admin |
| DELETE | `/api/users/{id}` | Delete user | Admin |

### Categories

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | `/api/categories` | Get all categories | Public |
| GET | `/api/categories/{id}` | Get category by ID | Public |
| GET | `/api/categories/search?q={query}` | Search categories | Public |
| POST | `/api/categories` | Create category | Admin |
| PUT | `/api/categories/{id}` | Update category | Admin |
| DELETE | `/api/categories/{id}` | Delete category | Admin |

### Products

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | `/api/products` | Get all products (paginated) | Public |
| GET | `/api/products/{id}` | Get product by ID | Public |
| GET | `/api/products/category/{categoryId}` | Get products by category | Public |
| GET | `/api/products/search?q={query}` | Search products | Public |
| POST | `/api/products` | Create product | Admin |
| PUT | `/api/products/{id}` | Update product | Admin |
| DELETE | `/api/products/{id}` | Soft delete product | Admin |

### Shopping Cart

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | `/api/cart` | Get user's cart | Authenticated |
| POST | `/api/cart/items` | Add item to cart | Authenticated |
| PUT | `/api/cart/items/{itemId}` | Update cart item quantity | Authenticated |
| DELETE | `/api/cart/items/{itemId}` | Remove item from cart | Authenticated |
| DELETE | `/api/cart` | Clear cart | Authenticated |

## Request/Response Examples

### Register User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123",
    "firstName": "John",
    "lastName": "Doe"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'
```

Response:
```json
{
  "success": true,
  "message": "Login successful",
  "timestamp": "2024-01-20T10:30:00",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "email": "user@example.com",
    "role": "CUSTOMER"
  }
}
```

### Get Products (with pagination)
```bash
curl -X GET "http://localhost:8080/api/products?page=0&size=10&sortBy=name&sortDir=ASC"
```

### Add to Cart (requires authentication)
```bash
curl -X POST http://localhost:8080/api/cart/items \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {your-jwt-token}" \
  -d '{
    "productId": 1,
    "quantity": 2
  }'
```

## Project Structure

```
src/main/java/com/ecommerce/
├── auth/                      # Authentication module
│   ├── controller/           # Auth endpoints
│   ├── dto/                  # Login, register DTOs
│   ├── jwt/                  # JWT provider & filter
│   └── service/              # Auth business logic
├── user/                      # User management
│   ├── controller/
│   ├── dto/
│   ├── mapper/
│   ├── model/
│   ├── repository/
│   └── service/
├── category/                  # Category management
├── product/                   # Product catalog
├── cart/                      # Shopping cart
└── common/                    # Shared components
    ├── config/               # Security, CORS, Swagger
    ├── exception/            # Custom exceptions
    └── response/             # API response wrapper
```

## Configuration Profiles

### Development (`application-dev.yml`)
- H2 in-memory database
- Auto DDL: create-drop
- Sample data initialization
- H2 console enabled at `/h2-console`

### Production (`application-prod.yml`)
- PostgreSQL database
- DDL validation only
- Production-ready settings

## Security

- All passwords are encrypted using BCrypt
- JWT tokens expire after 24 hours
- Stateless session management
- CORS configured for common frontend ports (3000, 4200, 8080)
- Role-based endpoint protection

## Health & Monitoring

Spring Boot Actuator endpoints:
```
http://localhost:8080/actuator/health
http://localhost:8080/actuator/info
```

## Build & Test

### Build
```bash
./mvnw clean install
```

### Run Tests
```bash
./mvnw test
```

### Package
```bash
./mvnw package
```

The executable JAR will be created in `target/` directory.

## Environment Variables

You can override configuration using environment variables:

```bash
export SPRING_PROFILES_ACTIVE=prod
export JWT_SECRET=your-secret-key
export JWT_EXPIRATION=86400000
export DB_URL=jdbc:postgresql://localhost:5432/ecommerce_db
export DB_USERNAME=postgres
export DB_PASSWORD=password
```

## License

This project is licensed under the MIT License.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request
