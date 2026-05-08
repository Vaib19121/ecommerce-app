package com.ecommerce.product.repository;

import com.ecommerce.product.model.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;

/**
 * JPA Specifications for dynamic Product filtering
 */
public class ProductSpecification {

    private ProductSpecification() {
        // Utility class
    }

    /**
     * Filter products by active status (always true for public queries)
     */
    public static Specification<Product> isActive() {
        return (root, query, cb) -> cb.isTrue(root.get("active"));
    }

    /**
     * Filter products by category names
     */
    public static Specification<Product> hasCategoryNames(List<String> categoryNames) {
        if (categoryNames == null || categoryNames.isEmpty()) {
            return null;
        }
        return (root, query, cb) -> root.get("category").get("name").in(categoryNames);
    }

    /**
     * Filter products by brand names
     */
    public static Specification<Product> hasBrands(List<String> brands) {
        if (brands == null || brands.isEmpty()) {
            return null;
        }
        return (root, query, cb) -> cb.lower(root.get("brand")).in(
                brands.stream().map(String::toLowerCase).toList()
        );
    }

    /**
     * Filter products by color names
     */
    public static Specification<Product> hasColors(List<String> colors) {
        if (colors == null || colors.isEmpty()) {
            return null;
        }
        return (root, query, cb) -> {
            var colorJoin = root.join("colors");
            return cb.lower(colorJoin.get("name")).in(
                    colors.stream().map(String::toLowerCase).toList()
            );
        };
    }

    /**
     * Filter products by size
     */
    public static Specification<Product> hasSizes(List<String> sizes) {
        if (sizes == null || sizes.isEmpty()) {
            return null;
        }
        return (root, query, cb) -> {
            var sizeElement = root.get("sizes");
            return sizeElement.in(sizes);
        };
    }

    /**
     * Filter products by price range
     */
    public static Specification<Product> priceIsBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, cb) -> {
            var predicates = new java.util.ArrayList<>();
            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }
            return predicates.isEmpty() ? null : cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }

    /**
     * Filter products that have stock (in stock)
     */
    public static Specification<Product> inStock() {
        return (root, query, cb) -> cb.greaterThan(root.get("stockQuantity"), 0);
    }

    /**
     * Filter products by new status
     */
    public static Specification<Product> isNew(Boolean isNew) {
        if (isNew == null || !isNew) {
            return null;
        }
        return (root, query, cb) -> cb.isTrue(root.get("isNew"));
    }

    /**
     * Filter products with free shipping
     */
    public static Specification<Product> hasFreeShipping(Boolean freeShipping) {
        if (freeShipping == null || !freeShipping) {
            return null;
        }
        return (root, query, cb) -> cb.isTrue(root.get("freeShipping"));
    }

    /**
     * Search products by name or description
     */
    public static Specification<Product> search(String query) {
        if (query == null || query.isBlank()) {
            return null;
        }
        String searchTerm = "%" + query.toLowerCase() + "%";
        return (root, criteriaQuery, cb) -> cb.or(
                cb.like(cb.lower(root.get("name")), searchTerm),
                cb.like(cb.lower(root.get("description")), searchTerm)
        );
    }

    /**
     * Combine all specifications with AND logic
     */
    public static Specification<Product> combine(Specification<Product>... specs) {
        Specification<Product> result = null;
        for (Specification<Product> spec : specs) {
            if (spec != null) {
                if (result == null) {
                    result = spec;
                } else {
                    result = result.and(spec);
                }
            }
        }
        return result != null ? result : isActive();
    }
}
