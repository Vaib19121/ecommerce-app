---
description: "Coding guidelines for DTOs (Data Transfer Objects) in this e-commerce application"
applyTo:
  - "**/dto/*.java"
---

# DTO Guidelines

## Purpose of DTOs

DTOs serve as the contract between client and server:
- **Decouples** API from internal entity structure
- **Controls** what data is exposed
- **Validates** incoming data
- **Simplifies** API responses (no circular references, lazy loading issues)

## Template Structures

### Response DTO (using record)
```java
package com.ecommerce.{feature}.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record {Feature}Dto(
    Long id,
    String name,
    String description,
    BigDecimal price,
    Integer stock,
    Long categoryId,
    String categoryName,
    LocalDateTime createdDate
) {}
```

### Create Request DTO
```java
package com.ecommerce.{feature}.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record {Feature}CreateRequest(
    
    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    String name,
    
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    String description,
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Price must have at most 8 integer digits and 2 decimal places")
    BigDecimal price,
    
    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock cannot be negative")
    Integer stock,
    
    @NotNull(message = "Category ID is required")
    Long categoryId
) {}
```

### Update Request DTO
```java
package com.ecommerce.{feature}.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record {Feature}UpdateRequest(
    
    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    String name,
    
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    String description,
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    BigDecimal price,
    
    @Min(value = 0, message = "Stock cannot be negative")
    Integer stock
    
    // Note: categoryId might be excluded if it shouldn't change on update
) {}
```

### Class-based DTO (when mutability or inheritance needed)
```java
package com.ecommerce.{feature}.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class {Feature}Dto {
    
    private Long id;
    
    @NotBlank(message = "Name is required")
    private String name;
    
    private String description;
    private BigDecimal price;
    
    // Nested DTO for related entity
    private CategorySummaryDto category;
}

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class CategorySummaryDto {
    private Long id;
    private String name;
}
```

## Validation Annotations

### Common Constraints

**String validation:**
```java
@NotNull                    // Cannot be null
@NotBlank                   // Cannot be null, empty, or whitespace
@NotEmpty                   // Cannot be null or empty (but whitespace ok)
@Size(min = 3, max = 100)   // Length constraints
@Email                      // Valid email format
@Pattern(regexp = "...")    // Custom regex pattern
```

**Numeric validation:**
```java
@NotNull                               // Cannot be null
@Min(value = 0)                        // Minimum value
@Max(value = 100)                      // Maximum value
@DecimalMin(value = "0.0", inclusive = false)  // Greater than
@DecimalMax(value = "1000.0")          // Less than or equal
@Positive                              // Must be positive
@PositiveOrZero                        // Must be >= 0
@Negative                              // Must be negative
@Digits(integer = 8, fraction = 2)     // Digit constraints
```

**Date/Time validation:**
```java
@NotNull                    // Cannot be null
@Past                       // Must be in the past
@PastOrPresent              // Must be in past or present
@Future                     // Must be in the future
@FutureOrPresent            // Must be in future or present
```

**Collection validation:**
```java
@NotNull                    // Collection cannot be null
@NotEmpty                   // Collection cannot be empty
@Size(min = 1, max = 10)    // Collection size constraints
```

**Boolean validation:**
```java
@NotNull                    // Cannot be null
@AssertTrue                 // Must be true
@AssertFalse                // Must be false
```

### Custom Messages
Always provide user-friendly messages:
```java
@NotBlank(message = "Name is required")
@Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
@Email(message = "Please provide a valid email address")
@Min(value = 0, message = "Quantity cannot be negative")
```

### Nested Object Validation
```java
public record OrderCreateRequest(
    
    @NotNull(message = "Shipping address is required")
    @Valid  // Validates nested object
    AddressDto shippingAddress,
    
    @NotEmpty(message = "Order must contain at least one item")
    @Valid  // Validates each item in list
    List<OrderItemDto> items
) {}

public record AddressDto(
    @NotBlank(message = "Street is required")
    String street,
    
    @NotBlank(message = "City is required")
    String city,
    
    @NotBlank(message = "ZIP code is required")
    @Pattern(regexp = "\\d{5}", message = "ZIP code must be 5 digits")
    String zipCode
) {}
```

## DTO Types

### 1. Response DTO
**Purpose**: Return data to client
**Naming**: `{Entity}Dto`
**Validation**: None required

```java
public record ProductDto(
    Long id,
    String name,
    String description,
    BigDecimal price,
    Integer stock,
    String categoryName,
    LocalDateTime createdDate
) {}
```

### 2. Create Request DTO
**Purpose**: Create new entity
**Naming**: `{Entity}CreateRequest`
**Validation**: Required

```java
public record ProductCreateRequest(
    @NotBlank String name,
    @NotNull BigDecimal price,
    @NotNull Long categoryId
) {}
```

### 3. Update Request DTO
**Purpose**: Update existing entity
**Naming**: `{Entity}UpdateRequest`
**Validation**: Required (may differ from create)

```java
public record ProductUpdateRequest(
    @NotBlank String name,
    @NotNull BigDecimal price,
    // Note: categoryId might be excluded if immutable
) {}
```

### 4. Summary/List DTO
**Purpose**: Lightweight DTO for lists/dropdowns
**Naming**: `{Entity}SummaryDto` or `{Entity}ListDto`

```java
public record ProductSummaryDto(
    Long id,
    String name,
    BigDecimal price
) {}
```

### 5. Search/Filter DTO
**Purpose**: Search criteria
**Naming**: `{Entity}SearchRequest` or `{Entity}FilterDto`

```java
public record ProductSearchRequest(
    String query,
    Long categoryId,
    BigDecimal minPrice,
    BigDecimal maxPrice,
    Boolean inStock
) {}
```

## Record vs Class

### Use Records When:
- Immutable data carrier
- Simple structure (no inheritance)
- Read-only response DTOs
- Request DTOs with validation

**Advantages:**
- Concise syntax
- Immutable by default
- Built-in equals/hashCode/toString
- Clear intent: data carrier

### Use Classes When:
- Need mutability
- Require inheritance
- Complex builder patterns
- Default values needed

## Common Patterns

### Flattening Nested Entities
```java
// Entity structure (nested)
Product {
    id, name, price
    Category category {
        id, name
    }
}

// Flattened DTO
public record ProductDto(
    Long id,
    String name,
    BigDecimal price,
    Long categoryId,       // Flattened
    String categoryName    // Flattened
) {}
```

### Pagination Response
```java
public record PageResponse<T>(
    List<T> content,
    int pageNumber,
    int pageSize,
    long totalElements,
    int totalPages,
    boolean first,
    boolean last
) {
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.isFirst(),
            page.isLast()
        );
    }
}
```

### Different Validation Rules for Create/Update
```java
// Create: password required
public record UserCreateRequest(
    @NotBlank @Email String email,
    @NotBlank @Size(min = 8) String password,
    @NotBlank String firstName,
    @NotBlank String lastName
) {}

// Update: password optional (only if changing)
public record UserUpdateRequest(
    @NotBlank String firstName,
    @NotBlank String lastName,
    @Size(min = 8) String password  // Optional
) {}
```

### Conditional Validation
```java
public record OrderCreateRequest(
    @NotNull OrderType orderType,
    String giftMessage,  // Required if orderType == GIFT
    
    @AssertTrue(message = "Gift message is required for gift orders")
    default boolean isGiftMessageValid() {
        if (orderType == OrderType.GIFT) {
            return giftMessage != null && !giftMessage.isBlank();
        }
        return true;
    }
) {}
```

## Anti-Patterns (Avoid)

❌ **Exposing entities directly**
```java
// BAD - returns entity
@GetMapping("/{id}")
public Product getProduct(@PathVariable Long id) {
    return productService.findById(id);
}
```

✅ **Use DTOs**
```java
// GOOD - returns DTO
@GetMapping("/{id}")
public ResponseEntity<ApiResponse<ProductDto>> getProduct(@PathVariable Long id) {
    ProductDto product = productService.getProductById(id);
    return ResponseEntity.ok(ApiResponse.success("Product retrieved", product));
}
```

❌ **One DTO for everything**
```java
// BAD - same DTO for create, update, and response
public class ProductDto {
    private Long id;           // Not needed in create request
    private String name;
    private LocalDateTime createdDate;  // Not needed in requests
}
```

✅ **Separate DTOs per use case**
```java
// GOOD - specific DTOs
public record ProductDto(Long id, String name, LocalDateTime createdDate) {}
public record ProductCreateRequest(@NotBlank String name) {}
public record ProductUpdateRequest(@NotBlank String name) {}
```

❌ **Validation in service layer**
```java
// BAD - manual validation
public ProductDto createProduct(ProductCreateRequest request) {
    if (request.name() == null || request.name().isBlank()) {
        throw new ValidationException("Name is required");
    }
    // ...
}
```

✅ **Validation in DTO**
```java
// GOOD - declarative validation
public record ProductCreateRequest(
    @NotBlank(message = "Name is required")
    String name
) {}

// Controller triggers validation
public ResponseEntity<ApiResponse<ProductDto>> createProduct(
        @Valid @RequestBody ProductCreateRequest request) {
    // Validation happens automatically
}
```

❌ **Circular references**
```java
// BAD - circular DTO references
public class CategoryDto {
    private List<ProductDto> products;
}

public class ProductDto {
    private CategoryDto category;  // Circular!
}
```

✅ **Flatten or use summaries**
```java
// GOOD - flattened
public record ProductDto(
    Long id,
    String name,
    Long categoryId,
    String categoryName
) {}

// Or use summary
public record CategorySummaryDto(Long id, String name) {}

public record ProductDto(
    Long id,
    String name,
    CategorySummaryDto category
) {}
```

## Testing

DTOs are typically tested indirectly through controller/service tests. Validation can be tested:

```java
class ProductCreateRequestValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldRejectBlankName() {
        ProductCreateRequest request = new ProductCreateRequest(
            "",  // blank name
            BigDecimal.TEN,
            1L
        );

        Set<ConstraintViolation<ProductCreateRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations)
            .extracting(ConstraintViolation::getMessage)
            .contains("Name is required");
    }

    @Test
    void shouldRejectNegativePrice() {
        ProductCreateRequest request = new ProductCreateRequest(
            "Product",
            BigDecimal.valueOf(-10),  // negative price
            1L
        );

        Set<ConstraintViolation<ProductCreateRequest>> violations = validator.validate(request);

        assertThat(violations)
            .extracting(ConstraintViolation::getMessage)
            .contains("Price must be greater than 0");
    }
}
```

## Checklist

When creating DTOs:
- [ ] Use records for immutable data carriers
- [ ] Separate response, create, and update DTOs
- [ ] Apply validation annotations with messages
- [ ] Flatten nested structures when appropriate
- [ ] No circular references
- [ ] Clear naming convention (Dto, CreateRequest, UpdateRequest)
- [ ] @Valid for nested object validation
- [ ] Consider creating summary DTOs for lists
- [ ] No business logic in DTOs
- [ ] Document any special validation rules
