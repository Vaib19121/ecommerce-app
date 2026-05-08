---
description: "Coding guidelines for Spring Boot REST controllers in this e-commerce application"
applyTo:
  - "**/controller/*Controller.java"
---

# Controller Guidelines

## Template Structure

```java
package com.ecommerce.{feature}.controller;

import com.ecommerce.common.response.ApiResponse;
import com.ecommerce.{feature}.dto.*;
import com.ecommerce.{feature}.service.{Feature}Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/{resources}")
@RequiredArgsConstructor
@Tag(name = "{Feature} Management", description = "APIs for managing {resources}")
public class {Feature}Controller {

    private final {Feature}Service {feature}Service;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // or 'CUSTOMER' as appropriate
    @Operation(summary = "Create {feature}", description = "Detailed description")
    public ResponseEntity<ApiResponse<{Feature}Dto>> create{Feature}(
            @Valid @RequestBody {Feature}CreateRequest request) {
        {Feature}Dto {feature} = {feature}Service.create{Feature}(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("{Feature} created successfully", {feature}));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get {feature} by ID")
    public ResponseEntity<ApiResponse<{Feature}Dto>> get{Feature}ById(@PathVariable Long id) {
        {Feature}Dto {feature} = {feature}Service.get{Feature}ById(id);
        return ResponseEntity.ok(ApiResponse.success("{Feature} retrieved successfully", {feature}));
    }

    @GetMapping
    @Operation(summary = "Get all {resources}", description = "With pagination and sorting")
    public ResponseEntity<ApiResponse<Page<{Feature}Dto>>> getAll{Features}(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("DESC") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<{Feature}Dto> {features} = {feature}Service.getAll{Features}(pageable);
        
        return ResponseEntity.ok(ApiResponse.success("{Features} retrieved successfully", {features}));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update {feature}")
    public ResponseEntity<ApiResponse<{Feature}Dto>> update{Feature}(
            @PathVariable Long id,
            @Valid @RequestBody {Feature}UpdateRequest request) {
        {Feature}Dto {feature} = {feature}Service.update{Feature}(id, request);
        return ResponseEntity.ok(ApiResponse.success("{Feature} updated successfully", {feature}));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete {feature}", description = "Soft delete (sets active=false)")
    public ResponseEntity<ApiResponse<Void>> delete{Feature}(@PathVariable Long id) {
        {feature}Service.delete{Feature}(id);
        return ResponseEntity.ok(ApiResponse.success("{Feature} deleted successfully", null));
    }
}
```

## Required Rules

### 1. Class Annotations
- `@RestController` - Marks as REST controller
- `@RequestMapping("/api/{resource}")` - Base path
- `@RequiredArgsConstructor` - Constructor injection
- `@Tag(name = "...", description = "...")` - Swagger documentation

### 2. Security Annotations
- Apply `@PreAuthorize` on methods requiring authorization
- Common roles: `'ADMIN'`, `'CUSTOMER'`
- No annotation = public endpoint

### 3. Method Annotations
- `@PostMapping` - Create operations (return 201 CREATED)
- `@GetMapping` - Read operations (return 200 OK)
- `@PutMapping` - Full update (return 200 OK)
- `@PatchMapping` - Partial update (return 200 OK)
- `@DeleteMapping` - Delete operations (return 200 OK)
- `@Operation(summary = "...", description = "...")` - Swagger docs

### 4. Input Validation
- Use `@Valid` for request body validation
- Use `@PathVariable` for URL parameters
- Use `@RequestParam` for query parameters
- Provide defaults for pagination: page=0, size=10

### 5. Response Format
- Always return `ResponseEntity<ApiResponse<T>>`
- Use `ApiResponse.success(message, data)` for success
- Set appropriate HTTP status:
  - 200 OK - successful GET, PUT, PATCH, DELETE
  - 201 CREATED - successful POST
  - 204 NO CONTENT - successful DELETE with no body (rare)

### 6. Pagination & Sorting
- Use `Pageable` for list endpoints
- Accept: `page`, `size`, `sortBy`, `sortDir` parameters
- Create `Sort` and `PageRequest` in controller
- Return `Page<Dto>` wrapped in ApiResponse

### 7. Error Handling
- Let service layer throw custom exceptions
- Don't catch exceptions in controller
- GlobalExceptionHandler will handle them

## Naming Conventions

- **Class**: `{Feature}Controller` (e.g., `ProductController`)
- **Methods**: `create{Feature}`, `get{Feature}ById`, `update{Feature}`, `delete{Feature}`
- **Request params**: camelCase (`sortBy`, not `sort_by`)
- **Path variables**: lowercase plural (`/api/products`, not `/api/product`)

## Common Patterns

### Search Endpoint
```java
@GetMapping("/search")
@Operation(summary = "Search {resources}")
public ResponseEntity<ApiResponse<Page<{Feature}Dto>>> search{Features}(
        @RequestParam String q,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
    
    Pageable pageable = PageRequest.of(page, size);
    Page<{Feature}Dto> results = {feature}Service.search{Features}(q, pageable);
    
    return ResponseEntity.ok(ApiResponse.success("Search completed", results));
}
```

### Get Current User's Data
```java
@GetMapping("/me")
@PreAuthorize("hasRole('CUSTOMER')")
@Operation(summary = "Get my {resource}")
public ResponseEntity<ApiResponse<{Feature}Dto>> getMy{Feature}(
        @AuthenticationPrincipal UserDetails userDetails) {
    
    String email = userDetails.getUsername();
    {Feature}Dto {feature} = {feature}Service.get{Feature}ByUserEmail(email);
    
    return ResponseEntity.ok(ApiResponse.success("{Feature} retrieved", {feature}));
}
```

### Bulk Operations
```java
@PostMapping("/bulk")
@PreAuthorize("hasRole('ADMIN')")
@Operation(summary = "Bulk create {resources}")
public ResponseEntity<ApiResponse<List<{Feature}Dto>>> bulkCreate{Features}(
        @Valid @RequestBody List<{Feature}CreateRequest> requests) {
    
    List<{Feature}Dto> {features} = {feature}Service.bulkCreate{Features}(requests);
    
    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success("{Features} created", {features}));
}
```

## Anti-Patterns (Avoid)

❌ **Business logic in controller**
```java
// BAD - business logic belongs in service
@PostMapping
public ResponseEntity<?> createProduct(@RequestBody ProductCreateRequest request) {
    Product product = new Product();
    product.setName(request.getName());
    // ... mapping logic
    productRepository.save(product);
    return ResponseEntity.ok(product);
}
```

✅ **Delegate to service**
```java
// GOOD - controller delegates to service
@PostMapping
public ResponseEntity<ApiResponse<ProductDto>> createProduct(
        @Valid @RequestBody ProductCreateRequest request) {
    ProductDto product = productService.createProduct(request);
    return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Product created", product));
}
```

❌ **Returning entities directly**
```java
// BAD - exposes internal entity structure
public ResponseEntity<Product> getProduct(@PathVariable Long id) {
    Product product = productService.findById(id);
    return ResponseEntity.ok(product);
}
```

✅ **Return DTOs**
```java
// GOOD - returns DTO with controlled structure
public ResponseEntity<ApiResponse<ProductDto>> getProduct(@PathVariable Long id) {
    ProductDto product = productService.getProductById(id);
    return ResponseEntity.ok(ApiResponse.success("Product retrieved", product));
}
```

❌ **Manual exception handling**
```java
// BAD - let GlobalExceptionHandler handle it
try {
    return productService.getProductById(id);
} catch (ResourceNotFoundException e) {
    return ResponseEntity.notFound().build();
}
```

✅ **Let exceptions propagate**
```java
// GOOD - exception propagates to GlobalExceptionHandler
public ResponseEntity<ApiResponse<ProductDto>> getProduct(@PathVariable Long id) {
    ProductDto product = productService.getProductById(id);
    return ResponseEntity.ok(ApiResponse.success("Product retrieved", product));
}
```

## Testing

Controllers should be tested with MockMvc:

```java
@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    void shouldCreateProduct() throws Exception {
        ProductCreateRequest request = new ProductCreateRequest(/* ... */);
        ProductDto expected = new ProductDto(/* ... */);
        
        when(productService.createProduct(any())).thenReturn(expected);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(expected.id()));
    }
}
```

## Checklist

When creating a new controller:
- [ ] Proper class annotations (@RestController, @RequestMapping, @RequiredArgsConstructor, @Tag)
- [ ] Inject service via constructor (final field)
- [ ] All methods have @Operation annotation
- [ ] Security annotations on protected endpoints
- [ ] Input validation with @Valid
- [ ] Consistent ApiResponse wrapper
- [ ] Pagination for list endpoints
- [ ] Appropriate HTTP status codes
- [ ] No business logic in controller
- [ ] No direct entity returns (use DTOs)
