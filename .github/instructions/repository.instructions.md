---
description: "Coding guidelines for Spring Data JPA repositories in this e-commerce application"
applyTo:
  - "**/repository/*Repository.java"
---

# Repository Guidelines

## Template Structure

```java
package com.ecommerce.{feature}.repository;

import com.ecommerce.{feature}.model.{Feature};
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface {Feature}Repository extends JpaRepository<{Feature}, Long> {

    // Query methods by convention
    Optional<{Feature}> findByIdAndActiveTrue(Long id);
    
    Page<{Feature}> findByActiveTrue(Pageable pageable);
    
    boolean existsByName(String name);
    
    List<{Feature}> findByUserId(Long userId);
    
    // Custom queries with @Query
    @Query("SELECT f FROM {Feature} f WHERE f.active = true " +
           "AND (LOWER(f.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(f.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<{Feature}> search(@Param("query") String query, Pageable pageable);
    
    // Native queries when needed
    @Query(value = "SELECT * FROM {features} WHERE category_id = :categoryId " +
                   "AND active = true ORDER BY created_date DESC LIMIT :limit",
           nativeQuery = true)
    List<{Feature}> findRecentByCategory(@Param("categoryId") Long categoryId, 
                                         @Param("limit") int limit);
}
```

## Required Rules

### 1. Extend JpaRepository
```java
public interface {Feature}Repository extends JpaRepository<{Feature}, Long> {
    // Entity type and ID type
}
```

Provides out-of-the-box:
- `save(entity)`, `saveAll(entities)`
- `findById(id)`, `findAll()`, `findAll(pageable)`
- `existsById(id)`, `count()`
- `deleteById(id)`, `delete(entity)`, `deleteAll()`

### 2. Method Naming Conventions

Spring Data generates queries from method names:

**Find operations:**
- `findBy{Property}` - Find by single property
- `findBy{Property}And{Property}` - Multiple conditions
- `findBy{Property}Or{Property}` - OR condition
- `findBy{Property}OrderBy{Property}Asc/Desc` - With sorting

**Existence checks:**
- `existsBy{Property}` - Returns boolean

**Count operations:**
- `countBy{Property}` - Returns long

**Delete operations:**
- `deleteBy{Property}` - Delete matching entities

### 3. Common Query Keywords

| Keyword | Example | JPQL |
|---------|---------|------|
| And | `findByNameAndPrice` | `WHERE name = ? AND price = ?` |
| Or | `findByNameOrDescription` | `WHERE name = ? OR description = ?` |
| Is, Equals | `findByIdIs` | `WHERE id = ?` |
| Between | `findByPriceBetween` | `WHERE price BETWEEN ? AND ?` |
| LessThan | `findByPriceLessThan` | `WHERE price < ?` |
| GreaterThan | `findByPriceGreaterThan` | `WHERE price > ?` |
| Like | `findByNameLike` | `WHERE name LIKE ?` |
| StartingWith | `findByNameStartingWith` | `WHERE name LIKE '?%'` |
| EndingWith | `findByNameEndingWith` | `WHERE name LIKE '%?'` |
| Containing | `findByNameContaining` | `WHERE name LIKE '%?%'` |
| In | `findByStatusIn` | `WHERE status IN (?)` |
| NotNull | `findByNameNotNull` | `WHERE name IS NOT NULL` |
| True/False | `findByActiveTrue` | `WHERE active = true` |
| OrderBy | `findByActiveOrderByCreatedDateDesc` | `ORDER BY created_date DESC` |

### 4. Custom Queries with @Query

Use JPQL for complex queries:
```java
@Query("SELECT p FROM Product p WHERE p.category.id = :categoryId " +
       "AND p.active = true AND p.stock > 0")
Page<Product> findAvailableProductsByCategory(@Param("categoryId") Long categoryId, 
                                               Pageable pageable);
```

Use native SQL when JPQL is insufficient:
```java
@Query(value = "SELECT * FROM products p " +
               "JOIN categories c ON p.category_id = c.id " +
               "WHERE c.name = :categoryName AND p.active = true",
       nativeQuery = true)
List<Product> findByNativeCategoryName(@Param("categoryName") String categoryName);
```

### 5. Soft Delete Support

For entities with `active` field:
```java
// Find only active
Optional<Product> findByIdAndActiveTrue(Long id);
Page<Product> findByActiveTrue(Pageable pageable);

// Find including inactive
Optional<Product> findById(Long id);
Page<Product> findAll(Pageable pageable);
```

## Common Patterns

### Basic CRUD with Soft Delete
```java
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Active entities only
    Optional<Product> findByIdAndActiveTrue(Long id);
    Page<Product> findByActiveTrue(Pageable pageable);
    List<Product> findByActiveTrueOrderByCreatedDateDesc();
    
    // Check existence
    boolean existsByIdAndActiveTrue(Long id);
    boolean existsByNameAndActiveTrue(String name);
}
```

### Search Functionality
```java
@Query("SELECT p FROM Product p WHERE p.active = true " +
       "AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
       "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')))")
Page<Product> search(@Param("query") String query, Pageable pageable);
```

### Filtering by Related Entity
```java
// By foreign key
Page<Product> findByCategoryIdAndActiveTrue(Long categoryId, Pageable pageable);

// By related entity property
@Query("SELECT p FROM Product p WHERE p.category.name = :categoryName AND p.active = true")
List<Product> findByCategoryName(@Param("categoryName") String categoryName);
```

### Aggregation Queries
```java
@Query("SELECT COUNT(p) FROM Product p WHERE p.category.id = :categoryId AND p.active = true")
long countActiveProductsByCategory(@Param("categoryId") Long categoryId);

@Query("SELECT SUM(p.stock) FROM Product p WHERE p.active = true")
Long getTotalStock();

@Query("SELECT AVG(p.price) FROM Product p WHERE p.category.id = :categoryId AND p.active = true")
Double getAveragePriceByCategory(@Param("categoryId") Long categoryId);
```

### Custom Projections
```java
// Interface projection
interface ProductSummary {
    Long getId();
    String getName();
    BigDecimal getPrice();
}

List<ProductSummary> findByActiveTrueOrderByPriceAsc();

// Class projection (DTO)
@Query("SELECT new com.ecommerce.product.dto.ProductStatsDto(p.category.name, COUNT(p), AVG(p.price)) " +
       "FROM Product p WHERE p.active = true GROUP BY p.category.name")
List<ProductStatsDto> getProductStatsByCategory();
```

### Batch Operations
```java
// Batch update
@Modifying
@Query("UPDATE Product p SET p.active = false WHERE p.category.id = :categoryId")
int deactivateProductsByCategory(@Param("categoryId") Long categoryId);

// Batch delete (use carefully)
@Modifying
@Query("DELETE FROM Product p WHERE p.active = false AND p.lastModifiedDate < :date")
int permanentlyDeleteInactiveProductsBefore(@Param("date") LocalDateTime date);
```

### Optimized Queries with Fetch Joins
```java
// Avoid N+1 queries
@Query("SELECT p FROM Product p LEFT JOIN FETCH p.category WHERE p.id = :id")
Optional<Product> findByIdWithCategory(@Param("id") Long id);

@Query("SELECT DISTINCT p FROM Product p " +
       "LEFT JOIN FETCH p.category " +
       "LEFT JOIN FETCH p.images " +
       "WHERE p.active = true")
List<Product> findAllActiveWithDetails();
```

## Anti-Patterns (Avoid)

❌ **Over-using native queries**
```java
// BAD - JPQL is more portable and type-safe
@Query(value = "SELECT * FROM products WHERE name = ?", nativeQuery = true)
List<Product> findByName(String name);
```

✅ **Use method naming or JPQL**
```java
// GOOD - method naming
List<Product> findByName(String name);

// GOOD - JPQL when complex
@Query("SELECT p FROM Product p WHERE p.name = :name AND p.active = true")
List<Product> findActiveByName(@Param("name") String name);
```

❌ **Business logic in repository**
```java
// BAD - business logic belongs in service
default Product createProductWithDefaults(String name) {
    Product product = new Product();
    product.setName(name);
    product.setActive(true);
    return save(product);
}
```

✅ **Pure data access in repository**
```java
// GOOD - repository only does data access
Optional<Product> findByName(String name);
```

❌ **Returning entities when projection suffices**
```java
// BAD - loads entire entity when only few fields needed
List<Product> findByActiveTrue();
```

✅ **Use projections for specific use cases**
```java
// GOOD - projection for dropdown lists
interface ProductOption {
    Long getId();
    String getName();
}
List<ProductOption> findByActiveTrue();
```

❌ **Forgetting @Modifying for updates/deletes**
```java
// BAD - missing @Modifying
@Query("UPDATE Product p SET p.stock = :stock WHERE p.id = :id")
void updateStock(@Param("id") Long id, @Param("stock") int stock);
```

✅ **Use @Modifying for DML**
```java
// GOOD
@Modifying
@Query("UPDATE Product p SET p.stock = :stock WHERE p.id = :id")
int updateStock(@Param("id") Long id, @Param("stock") int stock);
```

## Testing

Repository tests with `@DataJpaTest`:

```java
@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldFindActiveProductsByCategory() {
        // Given
        Category category = new Category();
        category.setName("Electronics");
        entityManager.persist(category);

        Product product1 = new Product();
        product1.setName("Laptop");
        product1.setCategory(category);
        product1.setActive(true);
        entityManager.persist(product1);

        Product product2 = new Product();
        product2.setName("Mouse");
        product2.setCategory(category);
        product2.setActive(false);
        entityManager.persist(product2);

        entityManager.flush();

        // When
        List<Product> results = productRepository
                .findByCategoryIdAndActiveTrue(category.getId(), Pageable.unpaged())
                .getContent();

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Laptop");
    }

    @Test
    void shouldSearchProducts() {
        // Given
        Product product = new Product();
        product.setName("Wireless Mouse");
        product.setDescription("Ergonomic design");
        product.setActive(true);
        entityManager.persistAndFlush(product);

        // When
        Page<Product> results = productRepository.search("wireless", Pageable.unpaged());

        // Then
        assertThat(results.getContent()).hasSize(1);
        assertThat(results.getContent().get(0).getName()).containsIgnoringCase("wireless");
    }
}
```

## Performance Tips

1. **Use pagination for large result sets**
   ```java
   Page<Product> findByActiveTrue(Pageable pageable);
   ```

2. **Fetch joins to avoid N+1**
   ```java
   @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category")
   List<Product> findAllWithCategory();
   ```

3. **Projections for specific fields**
   ```java
   interface ProductNameAndPrice {
       String getName();
       BigDecimal getPrice();
   }
   List<ProductNameAndPrice> findByActiveTrue();
   ```

4. **Batch operations**
   ```java
   @Modifying(clearAutomatically = true)
   @Query("UPDATE Product p SET p.stock = p.stock - :quantity WHERE p.id = :id")
   int decrementStock(@Param("id") Long id, @Param("quantity") int quantity);
   ```

## Checklist

When creating a new repository:
- [ ] Extends JpaRepository<Entity, ID>
- [ ] Use method naming conventions when possible
- [ ] Use @Query for complex queries
- [ ] Named parameters with @Param in queries
- [ ] Soft delete queries (findByActiveTrue)
- [ ] Pagination support where appropriate
- [ ] @Modifying for UPDATE/DELETE queries
- [ ] Fetch joins for commonly accessed relationships
- [ ] Tests with @DataJpaTest
