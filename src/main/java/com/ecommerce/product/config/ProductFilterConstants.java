package com.ecommerce.product.config;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * Product filter constants matching frontend data
 */
public class ProductFilterConstants {

    private ProductFilterConstants() {
        // Utility class
    }

    // Categories
    public static final List<String> CATEGORIES = Arrays.asList("Men", "Women", "Kids", "Accessories");

    // Brands
    public static final List<String> BRANDS = Arrays.asList(
            "Nike", "Adidas", "Puma", "Zara", "H&M", "Levi's", "Uniqlo", "Gap"
    );

    // Colors
    public static final List<ColorOption> COLORS = Arrays.asList(
            new ColorOption("Black", "black", "#1a1a1a"),
            new ColorOption("White", "white", "#f5f5f5"),
            new ColorOption("Red", "red", "#ef4444"),
            new ColorOption("Blue", "blue", "#3b82f6"),
            new ColorOption("Green", "green", "#22c55e"),
            new ColorOption("Yellow", "yellow", "#eab308"),
            new ColorOption("Purple", "purple", "#a855f7"),
            new ColorOption("Pink", "pink", "#ec4899"),
            new ColorOption("Gray", "gray", "#6b7280"),
            new ColorOption("Brown", "brown", "#92400e")
    );

    // Price Range
    public static final BigDecimal PRICE_MIN = BigDecimal.ZERO;
    public static final BigDecimal PRICE_MAX = new BigDecimal("5000");

    // Size options (common for clothing)
    public static final List<String> SIZES = Arrays.asList(
            "XS", "S", "M", "L", "XL", "XXL", "XXXL"
    );

    /**
     * Color option DTO
     */
    public static class ColorOption {
        public final String label;
        public final String value;
        public final String hex;

        public ColorOption(String label, String value, String hex) {
            this.label = label;
            this.value = value;
            this.hex = hex;
        }
    }
}
