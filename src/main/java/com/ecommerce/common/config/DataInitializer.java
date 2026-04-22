package com.ecommerce.common.config;

import com.ecommerce.category.model.Category;
import com.ecommerce.category.repository.CategoryRepository;
import com.ecommerce.product.model.Product;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.user.model.Role;
import com.ecommerce.user.model.User;
import com.ecommerce.user.repository.UserRepository;
import com.ecommerce.cart.model.Cart;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    @Profile("dev")
    public CommandLineRunner initData() {
        return args -> {
            log.info("Initializing development data...");

            // Create admin user if not exists
            if (!userRepository.existsByEmail("admin@ecommerce.com")) {
                User admin = new User();
                admin.setEmail("admin@ecommerce.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setFirstName("Admin");
                admin.setLastName("User");
                admin.setRole(Role.ADMIN);

                Cart adminCart = new Cart();
                adminCart.setUser(admin);
                admin.setCart(adminCart);

                userRepository.save(admin);
                log.info("Admin user created: admin@ecommerce.com / admin123");
            }

            // Create customer user if not exists
            if (!userRepository.existsByEmail("customer@ecommerce.com")) {
                User customer = new User();
                customer.setEmail("customer@ecommerce.com");
                customer.setPassword(passwordEncoder.encode("customer123"));
                customer.setFirstName("John");
                customer.setLastName("Doe");
                customer.setRole(Role.CUSTOMER);

                Cart customerCart = new Cart();
                customerCart.setUser(customer);
                customer.setCart(customerCart);

                userRepository.save(customer);
                log.info("Customer user created: customer@ecommerce.com / customer123");
            }

            // Create categories if not exist
            List<Category> categories = new ArrayList<>();
            
            if (!categoryRepository.existsByName("Electronics")) {
                Category electronics = new Category();
                electronics.setName("Electronics");
                electronics.setDescription("Electronic devices and accessories");
                categories.add(categoryRepository.save(electronics));
                log.info("Category created: Electronics");
            } else {
                categories.add(categoryRepository.findByName("Electronics").get());
            }

            if (!categoryRepository.existsByName("Clothing")) {
                Category clothing = new Category();
                clothing.setName("Clothing");
                clothing.setDescription("Fashion and apparel");
                categories.add(categoryRepository.save(clothing));
                log.info("Category created: Clothing");
            } else {
                categories.add(categoryRepository.findByName("Clothing").get());
            }

            if (!categoryRepository.existsByName("Books")) {
                Category books = new Category();
                books.setName("Books");
                books.setDescription("Books and publications");
                categories.add(categoryRepository.save(books));
                log.info("Category created: Books");
            } else {
                categories.add(categoryRepository.findByName("Books").get());
            }

            // Create sample products
            if (productRepository.count() == 0) {
                Category electronics = categories.get(0);
                Category clothing = categories.get(1);
                Category books = categories.get(2);

                // Electronics products
                Product laptop = new Product();
                laptop.setName("Laptop Pro 15");
                laptop.setDescription("High-performance laptop with 16GB RAM and 512GB SSD");
                laptop.setPrice(new BigDecimal("1299.99"));
                laptop.setStockQuantity(25);
                laptop.setImageUrl("https://via.placeholder.com/300x300?text=Laptop");
                laptop.setCategory(electronics);
                laptop.setActive(true);
                productRepository.save(laptop);

                Product smartphone = new Product();
                smartphone.setName("Smartphone X12");
                smartphone.setDescription("Latest smartphone with 5G connectivity and 128GB storage");
                smartphone.setPrice(new BigDecimal("899.99"));
                smartphone.setStockQuantity(50);
                smartphone.setImageUrl("https://via.placeholder.com/300x300?text=Smartphone");
                smartphone.setCategory(electronics);
                smartphone.setActive(true);
                productRepository.save(smartphone);

                Product headphones = new Product();
                headphones.setName("Wireless Headphones");
                headphones.setDescription("Noise-cancelling wireless headphones with 30-hour battery");
                headphones.setPrice(new BigDecimal("249.99"));
                headphones.setStockQuantity(100);
                headphones.setImageUrl("https://via.placeholder.com/300x300?text=Headphones");
                headphones.setCategory(electronics);
                headphones.setActive(true);
                productRepository.save(headphones);

                // Clothing products
                Product tshirt = new Product();
                tshirt.setName("Classic Cotton T-Shirt");
                tshirt.setDescription("100% cotton comfortable t-shirt in various colors");
                tshirt.setPrice(new BigDecimal("29.99"));
                tshirt.setStockQuantity(200);
                tshirt.setImageUrl("https://via.placeholder.com/300x300?text=T-Shirt");
                tshirt.setCategory(clothing);
                tshirt.setActive(true);
                productRepository.save(tshirt);

                Product jeans = new Product();
                jeans.setName("Slim Fit Jeans");
                jeans.setDescription("Modern slim-fit jeans made from premium denim");
                jeans.setPrice(new BigDecimal("79.99"));
                jeans.setStockQuantity(150);
                jeans.setImageUrl("https://via.placeholder.com/300x300?text=Jeans");
                jeans.setCategory(clothing);
                jeans.setActive(true);
                productRepository.save(jeans);

                // Books products
                Product novel = new Product();
                novel.setName("The Great Novel");
                novel.setDescription("Bestselling fiction novel by renowned author");
                novel.setPrice(new BigDecimal("19.99"));
                novel.setStockQuantity(300);
                novel.setImageUrl("https://via.placeholder.com/300x300?text=Novel");
                novel.setCategory(books);
                novel.setActive(true);
                productRepository.save(novel);

                Product cookbook = new Product();
                cookbook.setName("Modern Cooking Recipes");
                cookbook.setDescription("Collection of 500+ delicious and easy recipes");
                cookbook.setPrice(new BigDecimal("34.99"));
                cookbook.setStockQuantity(80);
                cookbook.setImageUrl("https://via.placeholder.com/300x300?text=Cookbook");
                cookbook.setCategory(books);
                cookbook.setActive(true);
                productRepository.save(cookbook);

                log.info("Sample products created: 7 products across 3 categories");
            }

            log.info("Development data initialization completed!");
        };
    }
}
