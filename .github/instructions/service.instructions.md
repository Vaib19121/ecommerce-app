---
description: "Coding guidelines for Spring service layer in this e-commerce application"
applyTo:
  - "**/service/*Service.java"
  - "**/service/*ServiceImpl.java"
---

# Service Layer Guidelines

## Interface + Implementation Pattern

Always create an interface and separate implementation:

### Service Interface
```java
package com.ecommerce.{feature}.service;

import com.ecommerce.{feature}.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface {Feature}Service {
    
    {Feature}Dto create{Feature}({Feature}CreateRequest request);
    
    {Feature}Dto update{Feature}(Long id, {Feature}UpdateRequest request);
    
    void delete{Feature}(Long id);
    
    {Feature}Dto get{Feature}ById(Long id);
    
    Page<{Feature}Dto> getAll{Features}(Pageable pageable);
    
    Page<{Feature}Dto> search{Features}(String query, Pageable pageable);
}
```

### Service Implementation
```java
package com.ecommerce.{feature}.service;

import com.ecommerce.common.exception.BusinessException;
import com.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.{feature}.dto.*;
import com.ecommerce.{feature}.mapper.{Feature}Mapper;
import com.ecommerce.{feature}.model.{Feature};
import com.ecommerce.{feature}.repository.{Feature}Repository;
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
public class {Feature}ServiceImpl implements {Feature}Service {

    private final {Feature}Repository {feature}Repository;
    private final {Feature}Mapper {feature}Mapper;

    @Override
    @Transactional
    public {Feature}Dto create{Feature}({Feature}CreateRequest request) {
        log.info("Creating {feature}: {}", request);
        
        // Business validation
        validateBusinessRules(request);
        
        // Map and save
        {Feature} {feature} = {feature}Mapper.toEntity(request);
        {Feature} saved = {feature}Repository.save({feature});
        
        log.info("{Feature} created with id: {}", saved.getId());
        return {feature}Mapper.toDto(saved);
    }

    @Override
    @Transactional
    public {Feature}Dto update{Feature}(Long id, {Feature}UpdateRequest request) {
        log.info("Updating {feature} with id: {}", id);
        
        {Feature} {feature} = findByIdOrThrow(id);
        
        // Business validation
        validateUpdateRules({feature}, request);
        
        // Update
        {feature}Mapper.updateEntityFromDto(request, {feature});
        {Feature} updated = {feature}Repository.save({feature});
        
        log.info("{Feature} updated: {}", id);
        return {feature}Mapper.toDto(updated);
    }

    @Override
    @Transactional
    public void delete{Feature}(Long id) {
        log.info("Deleting {feature} with id: {}", id);
        
        {Feature} {feature} = findByIdOrThrow(id);
        
        // Soft delete (set active = false)
        {feature}.setActive(false);
        {feature}Repository.save({feature});
        
        log.info("{Feature} deleted: {}", id);
    }

    @Override
    public {Feature}Dto get{Feature}ById(Long id) {
        log.debug("Retrieving {feature} with id: {}", id);
        
        {Feature} {feature} = findByIdOrThrow(id);
        return {feature}Mapper.toDto({feature});
    }

    @Override
    public Page<{Feature}Dto> getAll{Features}(Pageable pageable) {
        log.debug("Retrieving all {features} with pagination");
        
        return {feature}Repository.findByActiveTrue(pageable)
                .map({feature}Mapper::toDto);
    }

    @Override
    public Page<{Feature}Dto> search{Features}(String query, Pageable pageable) {
        log.debug("Searching {features} with query: {}", query);
        
        return {feature}Repository.search(query, pageable)
                .map({feature}Mapper::toDto);
    }

    // Helper methods
    private {Feature} findByIdOrThrow(Long id) {
        return {feature}Repository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "{Feature} not found with id: " + id));
    }

    private void validateBusinessRules({Feature}CreateRequest request) {
        // Example: Check uniqueness, validate relationships, etc.
        if ({feature}Repository.existsByName(request.getName())) {
            throw new BusinessException("{Feature} with this name already exists");
        }
    }

    private void validateUpdateRules({Feature} existing, {Feature}UpdateRequest request) {
        // Example: Prevent certain state transitions
        if (existing.isLocked()) {
            throw new BusinessException("Cannot update locked {feature}");
        }
    }
}
```

## Required Rules

### 1. Class Annotations
- `@Service` - Marks as Spring service
- `@RequiredArgsConstructor` - Constructor injection
- `@Transactional(readOnly = true)` - Default read-only transactions
- `@Slf4j` - Logging (optional but recommended)

### 2. Transaction Management
- Class-level: `@Transactional(readOnly = true)` for queries
- Method-level: `@Transactional` for write operations (create, update, delete)
- Read-only optimization for SELECT queries
- Automatic rollback on exceptions

### 3. Exception Handling
Use custom exceptions from `com.ecommerce.common.exception`:
- `ResourceNotFoundException` - Entity not found (404)
- `BusinessException` - Business rule violations (400)
- `UnauthorizedException` - Access denied (403)

### 4. Logging
- Use SLF4J with appropriate levels:
  - `log.info()` - Important state changes (create, update, delete)
  - `log.debug()` - Queries, retrievals
  - `log.warn()` - Unexpected but handled conditions
  - `log.error()` - Errors (usually in catch blocks)

### 5. Mapping
- Use MapStruct mapper for entity ↔ DTO conversion
- Never return entities directly to controller
- Always return DTOs

### 6. Validation
- Business logic validation in service layer
- Throw meaningful exceptions with descriptive messages
- Don't duplicate DTO validation (already done in controller)

## Common Patterns

### Find or Throw Helper
```java
private {Feature} findByIdOrThrow(Long id) {
    return {feature}Repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                    "{Feature} not found with id: " + id));
}

// For soft-delete entities
private {Feature} findActiveByIdOrThrow(Long id) {
    return {feature}Repository.findByIdAndActiveTrue(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                    "{Feature} not found with id: " + id));
}
```

### Existence Check
```java
private void ensureExists(Long id) {
    if (!{feature}Repository.existsById(id)) {
        throw new ResourceNotFoundException("{Feature} not found with id: " + id);
    }
}
```

### Business Rule Validation
```java
private void validateStockAvailability(Long productId, int quantity) {
    Product product = findProductByIdOrThrow(productId);
    
    if (product.getStock() < quantity) {
        throw new BusinessException(
                String.format("Insufficient stock for product %s. Available: %d, Requested: %d",
                        product.getName(), product.getStock(), quantity));
    }
}
```

### Relationship Validation
```java
private void validateCategoryExists(Long categoryId) {
    if (!categoryRepository.existsById(categoryId)) {
        throw new ResourceNotFoundException("Category not found with id: " + categoryId);
    }
}
```

### Soft Delete
```java
@Override
@Transactional
public void delete{Feature}(Long id) {
    {Feature} {feature} = findByIdOrThrow(id);
    {feature}.setActive(false);
    {feature}Repository.save({feature});
    log.info("{Feature} soft-deleted: {}", id);
}
```

### Bulk Operations
```java
@Override
@Transactional
public List<{Feature}Dto> bulkCreate{Features}(List<{Feature}CreateRequest> requests) {
    log.info("Bulk creating {} {features}", requests.size());
    
    List<{Feature}> entities = requests.stream()
            .map({feature}Mapper::toEntity)
            .toList();
    
    List<{Feature}> saved = {feature}Repository.saveAll(entities);
    
    return saved.stream()
            .map({feature}Mapper::toDto)
            .toList();
}
```

### Conditional Updates
```java
@Override
@Transactional
public {Feature}Dto updateStatus(Long id, Status newStatus) {
    {Feature} {feature} = findByIdOrThrow(id);
    
    // Validate state transition
    if (!{feature}.canTransitionTo(newStatus)) {
        throw new BusinessException(
                String.format("Cannot transition from %s to %s",
                        {feature}.getStatus(), newStatus));
    }
    
    {feature}.setStatus(newStatus);
    {Feature} updated = {feature}Repository.save({feature});
    
    log.info("{Feature} {} status updated to {}", id, newStatus);
    return {feature}Mapper.toDto(updated);
}
```

## Anti-Patterns (Avoid)

❌ **Returning entities**
```java
// BAD - exposes internal entity
public Product getProduct(Long id) {
    return productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Not found"));
}
```

✅ **Return DTOs**
```java
// GOOD - returns DTO
public ProductDto getProductById(Long id) {
    Product product = findByIdOrThrow(id);
    return productMapper.toDto(product);
}
```

❌ **Missing transaction annotation**
```java
// BAD - no transaction for write operation
public ProductDto createProduct(ProductCreateRequest request) {
    Product product = productMapper.toEntity(request);
    Product saved = productRepository.save(product);
    return productMapper.toDto(saved);
}
```

✅ **Use @Transactional**
```java
// GOOD - transaction ensures atomicity
@Transactional
public ProductDto createProduct(ProductCreateRequest request) {
    Product product = productMapper.toEntity(request);
    Product saved = productRepository.save(product);
    return productMapper.toDto(saved);
}
```

❌ **Generic exception messages**
```java
// BAD - not helpful for debugging
throw new ResourceNotFoundException("Not found");
```

✅ **Descriptive messages**
```java
// GOOD - clear context
throw new ResourceNotFoundException(
        "Product not found with id: " + id);
```

❌ **Business logic in controller**
```java
// BAD - validation belongs in service
// (This would be in controller)
if (product.getStock() < quantity) {
    throw new BusinessException("Insufficient stock");
}
```

✅ **Business logic in service**
```java
// GOOD - encapsulated in service method
private void validateStockAvailability(Product product, int quantity) {
    if (product.getStock() < quantity) {
        throw new BusinessException(
                String.format("Insufficient stock. Available: %d, Requested: %d",
                        product.getStock(), quantity));
    }
}
```

## Testing

Service layer should have comprehensive unit tests:

```java
@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void shouldCreateProduct() {
        // Given
        ProductCreateRequest request = new ProductCreateRequest(/* ... */);
        Product product = new Product(/* ... */);
        ProductDto expected = new ProductDto(/* ... */);

        when(productMapper.toEntity(request)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toDto(product)).thenReturn(expected);

        // When
        ProductDto result = productService.createProduct(request);

        // Then
        assertThat(result).isEqualTo(expected);
        verify(productRepository).save(product);
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        // Given
        Long id = 999L;
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> productService.getProductById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product not found with id: 999");
    }
}
```

## Checklist

When creating a new service:
- [ ] Create interface first
- [ ] Implementation has @Service, @RequiredArgsConstructor
- [ ] Class-level @Transactional(readOnly = true)
- [ ] Method-level @Transactional for writes
- [ ] Use @Slf4j for logging
- [ ] Helper methods for findOrThrow
- [ ] Business validation in service, not controller
- [ ] Always return DTOs, never entities
- [ ] Descriptive exception messages
- [ ] Appropriate log levels (info for writes, debug for reads)
- [ ] Unit tests for all public methods
