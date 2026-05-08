# E-Commerce Spring Boot Application - AI Assistant Instructions

## Project Overview

This is a production-ready e-commerce REST API built with Spring Boot 4.0.5, featuring:
- JWT-based authentication and authorization
- Product catalog with category management
- Shopping cart functionality
- Role-based access control (ADMIN/CUSTOMER)
- RESTful API design with comprehensive error handling

## Technology Stack

- **Framework**: Spring Boot 4.0.5
- **Java Version**: 17
- **Database**: PostgreSQL (prod), H2 (dev)
- **Security**: Spring Security + JWT (jjwt 0.12.5)
- **ORM**: Spring Data JPA + Hibernate
- **DTO Mapping**: MapStruct 1.6.3
- **API Docs**: Springdoc OpenAPI (Swagger)
- **Build Tool**: Maven

## Project Structure

```
com.ecommerce/
├── auth/           # Authentication & JWT handling
├── cart/           # Shopping cart management
├── category/       # Product categories
├── product/        # Product catalog
├── user/           # User management
└── common/         # Shared components
    ├── config/     # Security, CORS, Swagger
    ├── exception/  # Global exception handling
    ├── response/   # API response wrappers
    └── utils/      # Utility classes
```

## Architectural Patterns

### Layered Architecture
Each feature module follows a consistent 6-layer pattern:
1. **Controller** - REST endpoints, validation, security annotations
2. **Service** (Interface + Impl) - Business logic
3. **Repository** - Data access (Spring Data JPA)
4. **Model** - JPA entities
5. **DTO** - Data transfer objects (request/response)
6. **Mapper** - MapStruct entity-DTO conversion

### Naming Conventions
- **Entities**: Singular noun (e.g., `Product`, `Category`, `User`)
- **DTOs**: `{Entity}Dto`, `{Entity}CreateRequest`, `{Entity}UpdateRequest`
- **Services**: `{Entity}Service` (interface) + `{Entity}ServiceImpl`
- **Controllers**: `{Entity}Controller`
- **Repositories**: `{Entity}Repository`
- **Mappers**: `{Entity}Mapper` (MapStruct interface)

## Code Style Guidelines

### Controllers
- Use `@RestController` + `@RequestMapping("/api/{resource}")`
- Apply `@RequiredArgsConstructor` for dependency injection
- Use `@PreAuthorize` for role-based security
- Add Swagger annotations: `@Tag`, `@Operation`
- Return `ResponseEntity<ApiResponse<T>>` for consistent responses
- Validate inputs with `@Valid`
- Default pagination: page=0, size=10
- Support sorting with `sortBy` and `sortDir` parameters

### Services
- Create interface + implementation pattern
- Use `@Service` on implementation
- Apply `@Transactional` for write operations
- Throw custom exceptions for business logic violations:
  - `ResourceNotFoundException` - entity not found
  - `BusinessException` - business rule violations
  - `UnauthorizedException` - access denied

### Repositories
- Extend `JpaRepository<Entity, ID>`
- Use method naming conventions for queries
- Use `@Query` for complex queries
- Include soft delete support where applicable

### Models/Entities
- Use `@Entity`, `@Table(name = "...")` 
- Apply `@EntityListeners(AuditingEntityListener.class)` for auditing
- Use `@CreatedDate`, `@LastModifiedDate` for timestamps
- Apply `@Version` for optimistic locking
- Use `@Enumerated(EnumType.STRING)` for enums
- Implement proper `equals()` and `hashCode()` based on ID

### DTOs
- Keep DTOs flat and simple
- Use Jakarta validation annotations: `@NotNull`, `@NotBlank`, `@Email`, `@Size`, `@Min`, `@Max`
- Separate create/update request DTOs when validation differs
- Use records for immutable DTOs when appropriate

### Mappers
- Use MapStruct with `@Mapper(componentModel = "spring")`
- Define explicit mappings for complex transformations
- Handle null values appropriately
- Use `@Mapping` for field name differences

## Security Patterns

### JWT Authentication
- JWT tokens issued by `AuthController.login()`
- Tokens validated by `JwtAuthenticationFilter`
- User details loaded by `JwtUserDetailsService`
- Secret key configured in `application.yml` (environment variable in prod)

### Authorization
- Use `@PreAuthorize("hasRole('ADMIN')")` for admin-only endpoints
- Use `@PreAuthorize("hasRole('CUSTOMER')")` for customer endpoints
- Public endpoints: no security annotation
- Extract authenticated user: `@AuthenticationPrincipal UserDetails`

## API Response Format

All endpoints return standardized responses:

```json
{
  "success": true,
  "message": "Operation successful",
  "data": { /* payload */ },
  "timestamp": "2026-04-28T10:30:00"
}
```

## Error Handling

Global exception handler (`GlobalExceptionHandler`) catches:
- `ResourceNotFoundException` → 404
- `BusinessException` → 400
- `UnauthorizedException` → 403
- `MethodArgumentNotValidException` → 400 with field errors
- Generic exceptions → 500

## Database Conventions

- Use snake_case for table and column names
- Primary keys: `id` (Long, auto-generated)
- Audit fields: `created_date`, `last_modified_date`
- Soft deletes: `active` boolean flag
- Foreign keys: `{entity}_id` (e.g., `category_id`)
- Junction tables: `{entity1}_{entity2}` (e.g., `cart_items`)

## Configuration Profiles

- **dev**: H2 in-memory database, sample data auto-loaded, verbose logging
- **prod**: PostgreSQL, no sample data, optimized logging

## Testing Approach

- Use JUnit 5 for unit tests
- Use MockMvc for controller tests
- Use `@DataJpaTest` for repository tests
- Mock external dependencies with Mockito
- Aim for service layer test coverage

## Common Tasks

### Adding a New Feature Module
1. Create package: `com.ecommerce.{feature}`
2. Create subdirectories: controller, service, repository, model, dto, mapper
3. Define entity with JPA annotations
4. Create repository extending JpaRepository
5. Define DTOs with validation
6. Create MapStruct mapper
7. Implement service interface + implementation
8. Create controller with REST endpoints
9. Add security annotations as needed
10. Document with Swagger annotations

### Running the Application
```bash
# Development mode
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Production mode
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod

# Build
./mvnw clean install

# Run tests
./mvnw test
```

### Accessing API Documentation
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

## Development Preferences

- **Code Generation**: Prefer MapStruct over manual mapping
- **Validation**: Apply at DTO level, not in service layer
- **Logging**: Use SLF4J with appropriate log levels
- **Exception Messages**: Clear, user-friendly, no stack traces in responses
- **API Versioning**: Use URL versioning if needed (`/api/v2/...`)
- **Documentation**: Swagger annotations are mandatory for all endpoints

## Important Notes

- Never commit secrets or passwords to version control
- Use environment variables for sensitive configuration (JWT_SECRET, DB passwords)
- Always validate user input at the controller level
- Use pagination for list endpoints to prevent performance issues
- Implement soft deletes for data integrity (retain records with `active=false`)
- Apply optimistic locking for concurrent update scenarios
- Follow REST principles: use appropriate HTTP methods and status codes
