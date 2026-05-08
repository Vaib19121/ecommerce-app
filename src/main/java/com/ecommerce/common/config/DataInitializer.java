package com.ecommerce.common.config;

import com.ecommerce.category.model.Category;
import com.ecommerce.category.repository.CategoryRepository;
import com.ecommerce.product.model.Product;
import com.ecommerce.product.model.ProductImage;
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

            if (!categoryRepository.existsByName("Accessories")) {
                Category accessories = new Category();
                accessories.setName("Accessories");
                accessories.setDescription("Fashion accessories, bags, watches, and more");
                categories.add(categoryRepository.save(accessories));
                log.info("Category created: Accessories");
            } else {
                categories.add(categoryRepository.findByName("Accessories").get());
            }

            if (!categoryRepository.existsByName("Beauty")) {
                Category beauty = new Category();
                beauty.setName("Beauty");
                beauty.setDescription("Skincare, cosmetics, and beauty products");
                categories.add(categoryRepository.save(beauty));
                log.info("Category created: Beauty");
            } else {
                categories.add(categoryRepository.findByName("Beauty").get());
            }

            if (!categoryRepository.existsByName("Furniture")) {
                Category furniture = new Category();
                furniture.setName("Furniture");
                furniture.setDescription("Home and office furniture");
                categories.add(categoryRepository.save(furniture));
                log.info("Category created: Furniture");
            } else {
                categories.add(categoryRepository.findByName("Furniture").get());
            }

            // Create sample products
            if (productRepository.count() == 0) {
                Category electronics = categories.stream().filter(c -> c.getName().equals("Electronics")).findFirst().orElse(null);
                Category clothing = categories.stream().filter(c -> c.getName().equals("Clothing")).findFirst().orElse(null);
                Category books = categories.stream().filter(c -> c.getName().equals("Books")).findFirst().orElse(null);
                Category accessories = categories.stream().filter(c -> c.getName().equals("Accessories")).findFirst().orElse(null);
                Category beauty = categories.stream().filter(c -> c.getName().equals("Beauty")).findFirst().orElse(null);
                Category furniture = categories.stream().filter(c -> c.getName().equals("Furniture")).findFirst().orElse(null);

                // Original Electronics products
                Product laptop = new Product();
                laptop.setName("Laptop Pro 15");
                laptop.setDescription("High-performance laptop with 16GB RAM and 512GB SSD");
                laptop.setPrice(new BigDecimal("1299.99"));
                laptop.setStockQuantity(25);
                laptop.setCategory(electronics);
                laptop.setActive(true);
                addImage(laptop, "https://via.placeholder.com/300x300?text=Laptop");
                productRepository.save(laptop);

                Product smartphone = new Product();
                smartphone.setName("Smartphone X12");
                smartphone.setDescription("Latest smartphone with 5G connectivity and 128GB storage");
                smartphone.setPrice(new BigDecimal("899.99"));
                smartphone.setStockQuantity(50);
                smartphone.setCategory(electronics);
                smartphone.setActive(true);
                addImage(smartphone, "https://via.placeholder.com/300x300?text=Smartphone");
                productRepository.save(smartphone);

                Product headphones = new Product();
                headphones.setName("Wireless Headphones");
                headphones.setDescription("Noise-cancelling wireless headphones with 30-hour battery");
                headphones.setPrice(new BigDecimal("249.99"));
                headphones.setStockQuantity(100);
                headphones.setCategory(electronics);
                headphones.setActive(true);
                addImage(headphones, "https://via.placeholder.com/300x300?text=Headphones");
                productRepository.save(headphones);

                // Original Clothing products
                Product tshirt = new Product();
                tshirt.setName("Classic Cotton T-Shirt");
                tshirt.setDescription("100% cotton comfortable t-shirt in various colors");
                tshirt.setPrice(new BigDecimal("29.99"));
                tshirt.setStockQuantity(200);
                tshirt.setCategory(clothing);
                tshirt.setActive(true);
                addImage(tshirt, "https://via.placeholder.com/300x300?text=T-Shirt");
                productRepository.save(tshirt);

                Product jeans = new Product();
                jeans.setName("Slim Fit Jeans");
                jeans.setDescription("Modern slim-fit jeans made from premium denim");
                jeans.setPrice(new BigDecimal("79.99"));
                jeans.setStockQuantity(150);
                jeans.setCategory(clothing);
                jeans.setActive(true);
                addImage(jeans, "https://via.placeholder.com/300x300?text=Jeans");
                productRepository.save(jeans);

                // Original Books products
                Product novel = new Product();
                novel.setName("The Great Novel");
                novel.setDescription("Bestselling fiction novel by renowned author");
                novel.setPrice(new BigDecimal("19.99"));
                novel.setStockQuantity(300);
                novel.setCategory(books);
                novel.setActive(true);
                addImage(novel, "https://via.placeholder.com/300x300?text=Novel");
                productRepository.save(novel);

                Product cookbook = new Product();
                cookbook.setName("Modern Cooking Recipes");
                cookbook.setDescription("Collection of 500+ delicious and easy recipes");
                cookbook.setPrice(new BigDecimal("34.99"));
                cookbook.setStockQuantity(80);
                cookbook.setCategory(books);
                cookbook.setActive(true);
                addImage(cookbook, "https://via.placeholder.com/300x300?text=Cookbook");
                productRepository.save(cookbook);

                // New products from user request
                // Premium Leather Sneakers
                Product sneakers = new Product();
                sneakers.setName("Premium Leather Sneakers");
                sneakers.setBrand("UrbanStep");
                sneakers.setDescription("Premium leather sneakers with superior comfort and style");
                sneakers.setPrice(new BigDecimal("89.99"));
                sneakers.setOriginalPrice(new BigDecimal("129.99"));
                sneakers.setDiscountPercentage(31);
                sneakers.setStockQuantity(120);
                sneakers.setCategory(accessories);
                sneakers.setActive(true);
                addImage(sneakers, "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400&h=400&fit=crop&auto=format");
                productRepository.save(sneakers);

                // Minimalist Watch Pro
                Product watch = new Product();
                watch.setName("Minimalist Watch Pro");
                watch.setBrand("TimeCraft");
                watch.setDescription("Elegant minimalist design with precision timekeeping");
                watch.setPrice(new BigDecimal("199.00"));
                watch.setStockQuantity(85);
                watch.setIsNew(true);
                watch.setCategory(accessories);
                watch.setActive(true);
                addImage(watch, "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=400&h=400&fit=crop&auto=format");
                productRepository.save(watch);

                // Wireless Noise-Cancelling Headphones
                Product ncHeadphones = new Product();
                ncHeadphones.setName("Wireless Noise-Cancelling Headphones");
                ncHeadphones.setBrand("SoundWave");
                ncHeadphones.setDescription("Premium noise-cancelling headphones with immersive sound quality");
                ncHeadphones.setPrice(new BigDecimal("149.99"));
                ncHeadphones.setOriginalPrice(new BigDecimal("199.99"));
                ncHeadphones.setDiscountPercentage(25);
                ncHeadphones.setStockQuantity(200);
                ncHeadphones.setCategory(electronics);
                ncHeadphones.setActive(true);
                addImage(ncHeadphones, "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400&h=400&fit=crop&auto=format");
                productRepository.save(ncHeadphones);

                // Linen Summer Dress
                Product dress = new Product();
                dress.setName("Linen Summer Dress");
                dress.setBrand("Bloom & Co.");
                dress.setDescription("Breathable linen summer dress perfect for warm weather");
                dress.setPrice(new BigDecimal("59.99"));
                dress.setStockQuantity(150);
                dress.setCategory(clothing);
                dress.setActive(true);
                addImage(dress, "https://images.unsplash.com/photo-1585487000160-6ebcfceb0d03?w=400&h=400&fit=crop&auto=format");
                productRepository.save(dress);

                // Smart Backpack 30L
                Product backpack = new Product();
                backpack.setName("Smart Backpack 30L");
                backpack.setBrand("TrailBlaze");
                backpack.setDescription("Spacious 30L backpack with smart organizational features");
                backpack.setPrice(new BigDecimal("79.99"));
                backpack.setOriginalPrice(new BigDecimal("99.99"));
                backpack.setDiscountPercentage(20);
                backpack.setStockQuantity(95);
                backpack.setCategory(accessories);
                backpack.setActive(true);
                addImage(backpack, "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=400&h=400&fit=crop&auto=format");
                productRepository.save(backpack);

                // Bamboo Skincare Set
                Product skincare = new Product();
                skincare.setName("Bamboo Skincare Set");
                skincare.setBrand("PureGlow");
                skincare.setDescription("Complete natural skincare set with organic bamboo extracts");
                skincare.setPrice(new BigDecimal("44.99"));
                skincare.setStockQuantity(180);
                skincare.setCategory(beauty);
                skincare.setActive(true);
                addImage(skincare, "https://images.unsplash.com/photo-1570194065650-d99fb4b38233?w=400&h=400&fit=crop&auto=format");
                productRepository.save(skincare);

                // Ergonomic Office Chair
                Product chair = new Product();
                chair.setName("Ergonomic Office Chair");
                chair.setBrand("ComfortZone");
                chair.setDescription("Professional ergonomic office chair with lumbar support");
                chair.setPrice(new BigDecimal("299.00"));
                chair.setOriginalPrice(new BigDecimal("399.00"));
                chair.setDiscountPercentage(25);
                chair.setStockQuantity(40);
                chair.setCategory(furniture);
                chair.setActive(true);
                addImage(chair, "https://images.unsplash.com/photo-1580480055273-228ff5388ef8?w=400&h=400&fit=crop&auto=format");
                productRepository.save(chair);

                // Stainless Steel Water Bottle
                Product bottle = new Product();
                bottle.setName("Stainless Steel Water Bottle");
                bottle.setBrand("HydraMax");
                bottle.setDescription("Durable stainless steel water bottle with vacuum insulation");
                bottle.setPrice(new BigDecimal("34.99"));
                bottle.setStockQuantity(350);
                bottle.setCategory(accessories);
                bottle.setActive(true);
                addImage(bottle, "https://images.unsplash.com/photo-1602143407151-7111542de6e8?w=400&h=400&fit=crop&auto=format");
                productRepository.save(bottle);

                log.info("Sample products created: 15 products across 6 categories");
            }

            log.info("Development data initialization completed!");
        };
    }

    private void addImage(Product product, String url) {
        ProductImage image = new ProductImage();
        image.setProduct(product);
        image.setUrl(url);
        image.setDisplayOrder(0);
        product.getImages().add(image);
    }
}
