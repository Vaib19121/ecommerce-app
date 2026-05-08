---
description: "Coding guidelines for JPA entities/models in this e-commerce application"
applyTo:
  - "**/model/*.java"
  - "!**/model/*Dto.java"
---

# Entity/Model Guidelines

## Template Structure

```java
package com.ecommerce.{feature}.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "{features}")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class {Feature} {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stock = 0;

    @Column(nullable = false)
    private Boolean active = true;

    @Version
    private Long version;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime lastModifiedDate;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "{feature}", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RelatedEntity> relatedEntities = new ArrayList<>();

    // Custom equals and hashCode based on ID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        {Feature} {feature} = ({Feature}) o;
        return id != null && Objects.equals(id, {feature}.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    // Business methods
    public boolean canBeDeleted() {
        return active && relatedEntities.isEmpty();
    }

    public void deactivate() {
        this.active = false;
    }
}
```

## Required Rules

### 1. Class Annotations
```java
@Entity                                     // Marks as JPA entity
@Table(name = "{features}")                 // Table name (snake_case, plural)
@EntityListeners(AuditingEntityListener.class)  // Enables @CreatedDate/@LastModifiedDate
@Getter @Setter                             // Lombok getters/setters
@NoArgsConstructor                          // Required by JPA
@AllArgsConstructor                         // Optional: for builder pattern
@Builder                                    // Optional: builder pattern
```

### 2. Primary Key
```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```
- Always use `Long` for ID type
- Use `IDENTITY` strategy (auto-increment)
- Database generates values

### 3. Auditing Fields (Required)
```java
@CreatedDate
@Column(nullable = false, updatable = false)
private LocalDateTime createdDate;

@LastModifiedDate
@Column(nullable = false)
private LocalDateTime lastModifiedDate;
```
- Automatically managed by Spring Data JPA
- Requires `@EnableJpaAuditing` in main application class

### 4. Optimistic Locking
```java
@Version
private Long version;
```
- Prevents concurrent update conflicts
- Automatically incremented on each update
- Throws `OptimisticLockException` on conflict

### 5. Soft Delete Support
```java
@Column(nullable = false)
private Boolean active = true;
```
- Default: `true`
- Set to `false` instead of deleting record
- Filter queries: `findByActiveTrue()`

### 6. Column Definitions
```java
@Column(nullable = false, length = 100)         // String with max length
@Column(nullable = false, unique = true)         // Unique constraint
@Column(nullable = false, precision = 10, scale = 2)  // Decimal (money)
@Column(name = "email_address")                  // Custom column name
```

### 7. Enumerations
```java
@Enumerated(EnumType.STRING)
@Column(nullable = false, length = 20)
private OrderStatus status = OrderStatus.PENDING;
```
- Always use `EnumType.STRING` (not ORDINAL)
- Safer for database changes

### 8. Relationships

**Many-to-One (most common)**
```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "category_id", nullable = false)
private Category category;
```

**One-to-Many (inverse side)**
```java
@OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
private List<Product> products = new ArrayList<>();
```

**One-to-One**
```java
@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
@JoinColumn(name = "cart_id", referencedColumnName = "id")
private Cart cart;
```

**Many-to-Many**
```java
@ManyToMany
@JoinTable(
    name = "product_tags",
    joinColumns = @JoinColumn(name = "product_id"),
    inverseJoinColumns = @JoinColumn(name = "tag_id")
)
private Set<Tag> tags = new HashSet<>();
```

### 9. Fetch Types
- **LAZY** (default for collections): Load on access
- **EAGER**: Load immediately with parent

**Best practice**: Use LAZY by default, fetch eagerly in repository when needed.

```java
// Entity: LAZY
@ManyToOne(fetch = FetchType.LAZY)
private Category category;

// Repository: Fetch join when needed
@Query("SELECT p FROM Product p LEFT JOIN FETCH p.category WHERE p.id = :id")
Optional<Product> findByIdWithCategory(@Param("id") Long id);
```

### 10. Cascade Types
- **CascadeType.ALL**: All operations cascade
- **CascadeType.PERSIST**: Cascade save operations
- **CascadeType.MERGE**: Cascade update operations
- **CascadeType.REMOVE**: Cascade delete operations
- **CascadeType.REFRESH**: Cascade refresh operations
- **CascadeType.DETACH**: Cascade detach operations

**Use with care**: Only cascade when child entities cannot exist without parent.

### 11. Equals and HashCode
```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Product product = (Product) o;
    return id != null && Objects.equals(id, product.id);
}

@Override
public int hashCode() {
    return getClass().hashCode();
}
```
- **Critical** for collections and JPA
- Based on `id` field only
- Consistent across persistence states

## Common Patterns

### Money Values
```java
@Column(nullable = false, precision = 10, scale = 2)
private BigDecimal price;
```
- Use `BigDecimal`, never `double` or `float`
- `precision = 10`: total digits
- `scale = 2`: decimal places

### Email Fields
```java
@Column(nullable = false, unique = true, length = 100)
private String email;
```
- Mark as unique
- Validate in DTO layer with `@Email`

### Timestamps
```java
@Column(nullable = false, updatable = false)
private LocalDateTime orderDate = LocalDateTime.now();
```
- Use `LocalDateTime` (not `Date` or `Timestamp`)
- Set default in field initialization or constructor

### Bi-directional Relationships
```java
// Parent (Product)
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "category_id")
private Category category;

// Child (Category)
@OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
private List<Product> products = new ArrayList<>();

// Helper methods
public void setCategory(Category category) {
    this.category = category;
    if (category != null && !category.getProducts().contains(this)) {
        category.getProducts().add(this);
    }
}
```

### Composite Primary Key
```java
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItemId implements Serializable {
    private Long cartId;
    private Long productId;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItemId that = (CartItemId) o;
        return Objects.equals(cartId, that.cartId) &&
               Objects.equals(productId, that.productId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(cartId, productId);
    }
}

@Entity
@Table(name = "cart_items")
public class CartItem {
    @EmbeddedId
    private CartItemId id;
    
    // Other fields...
}
```

### Inheritance (Single Table Strategy)
```java
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
public abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String email;
    // Common fields
}

@Entity
@DiscriminatorValue("CUSTOMER")
public class Customer extends User {
    private String shippingAddress;
    // Customer-specific fields
}

@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends User {
    private String department;
    // Admin-specific fields
}
```

## Anti-Patterns (Avoid)

❌ **Using @Data from Lombok**
```java
// BAD - @Data includes toString/equals/hashCode with all fields
@Data
@Entity
public class Product {
    // Causes issues with lazy loading and circular references
}
```

✅ **Use specific Lombok annotations**
```java
// GOOD - explicit control
@Getter
@Setter
@NoArgsConstructor
@Entity
public class Product {
    // Custom equals/hashCode based on ID
}
```

❌ **EAGER fetching everywhere**
```java
// BAD - loads everything always
@ManyToOne(fetch = FetchType.EAGER)
private Category category;

@OneToMany(fetch = FetchType.EAGER)
private List<Product> products;
```

✅ **Use LAZY and fetch when needed**
```java
// GOOD - lazy by default
@ManyToOne(fetch = FetchType.LAZY)
private Category category;

// Fetch in repository when needed
@Query("SELECT p FROM Product p JOIN FETCH p.category WHERE p.id = :id")
Optional<Product> findByIdWithCategory(@Param("id") Long id);
```

❌ **Bi-directional without mappedBy**
```java
// BAD - creates two foreign keys
@Entity
public class Product {
    @ManyToOne
    private Category category;
}

@Entity
public class Category {
    @OneToMany
    private List<Product> products;
}
```

✅ **Use mappedBy on inverse side**
```java
// GOOD - single foreign key
@Entity
public class Product {
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}

@Entity
public class Category {
    @OneToMany(mappedBy = "category")
    private List<Product> products;
}
```

❌ **Exposing setters for collections**
```java
// BAD - breaks encapsulation
public void setProducts(List<Product> products) {
    this.products = products;
}
```

✅ **Provide add/remove methods**
```java
// GOOD - controlled access
public void addProduct(Product product) {
    products.add(product);
    product.setCategory(this);
}

public void removeProduct(Product product) {
    products.remove(product);
    product.setCategory(null);
}
```

## Testing

Entities are tested via repository tests or service tests. Unit tests for business methods:

```java
class ProductTest {

    @Test
    void shouldDeactivateProduct() {
        Product product = Product.builder()
                .name("Test Product")
                .active(true)
                .build();
        
        product.deactivate();
        
        assertThat(product.getActive()).isFalse();
    }

    @Test
    void shouldNotAllowDeletionWhenNotActive() {
        Product product = Product.builder()
                .active(false)
                .build();
        
        assertThat(product.canBeDeleted()).isFalse();
    }
}
```

## Database Naming Conventions

- **Tables**: plural, snake_case (`products`, `order_items`)
- **Columns**: snake_case (`created_date`, `user_id`)
- **Foreign keys**: `{entity}_id` (`category_id`, `user_id`)
- **Join tables**: `{entity1}_{entity2}` (`product_tags`)

## Checklist

When creating a new entity:
- [ ] @Entity, @Table with plural snake_case name
- [ ] @EntityListeners(AuditingEntityListener.class)
- [ ] @Getter, @Setter, @NoArgsConstructor
- [ ] Long id with @Id and @GeneratedValue
- [ ] @CreatedDate and @LastModifiedDate fields
- [ ] @Version for optimistic locking
- [ ] Boolean active field for soft delete
- [ ] Proper @Column definitions (nullable, length, etc.)
- [ ] EnumType.STRING for enums
- [ ] FetchType.LAZY for relationships
- [ ] Custom equals() and hashCode() based on id
- [ ] Initialize collections in field declaration
- [ ] Business methods where appropriate
