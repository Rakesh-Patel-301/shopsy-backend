package com.example.ecommerce.config;

import com.example.ecommerce.model.*;
import com.example.ecommerce.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        // Check if data already exists (both categories and users)
        // Temporarily disabled to force refresh with 40 products
        /*
        if (categoryRepository.count() > 0 && userRepository.count() > 0) {
            System.out.println("Data already initialized. Skipping...");
            return;
        }
        */

        // Clear existing data in correct order to avoid foreign key constraints
        System.out.println("Clearing existing data...");
        reviewRepository.deleteAll();   // Clear reviews first (references users and products)
        cartItemRepository.deleteAll(); // Clear cart items (references products)
        cartRepository.deleteAll();     // Then clear carts
        productRepository.deleteAll();  // Clear products
        categoryRepository.deleteAll(); // Clear categories
        userRepository.deleteAll();     // Clear users last

        // Create sample users first
        User user1 = new User();
        user1.setUsername("john_doe");
        user1.setEmail("john@example.com");
        user1.setPassword("password123");
        user1.setFirstName("John");
        user1.setLastName("Doe");
        user1.setPhone("123-456-7890");
        user1.setAddress("123 Main St, City, State 12345");
        user1.setRole(User.UserRole.CUSTOMER);
        user1.setEnabled(true);
        userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("sarah_smith");
        user2.setEmail("sarah@example.com");
        user2.setPassword("password123");
        user2.setFirstName("Sarah");
        user2.setLastName("Smith");
        user2.setPhone("234-567-8901");
        user2.setAddress("456 Oak Ave, City, State 12345");
        user2.setRole(User.UserRole.CUSTOMER);
        user2.setEnabled(true);
        userRepository.save(user2);

        User user3 = new User();
        user3.setUsername("mike_johnson");
        user3.setEmail("mike@example.com");
        user3.setPassword("password123");
        user3.setFirstName("Mike");
        user3.setLastName("Johnson");
        user3.setPhone("345-678-9012");
        user3.setAddress("789 Pine Rd, City, State 12345");
        user3.setRole(User.UserRole.CUSTOMER);
        user3.setEnabled(true);
        userRepository.save(user3);

        User user4 = new User();
        user4.setUsername("lisa_wilson");
        user4.setEmail("lisa@example.com");
        user4.setPassword("password123");
        user4.setFirstName("Lisa");
        user4.setLastName("Wilson");
        user4.setPhone("456-789-0123");
        user4.setAddress("321 Elm St, City, State 12345");
        user4.setRole(User.UserRole.CUSTOMER);
        user4.setEnabled(true);
        userRepository.save(user4);

        User user5 = new User();
        user5.setUsername("david_brown");
        user5.setEmail("david@example.com");
        user5.setPassword("password123");
        user5.setFirstName("David");
        user5.setLastName("Brown");
        user5.setPhone("567-890-1234");
        user5.setAddress("654 Maple Dr, City, State 12345");
        user5.setRole(User.UserRole.CUSTOMER);
        user5.setEnabled(true);
        userRepository.save(user5);

        // Create admin user
        User adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@example.com");
        adminUser.setPassword("admin123");
        adminUser.setFirstName("Admin");
        adminUser.setLastName("User");
        adminUser.setPhone("999-999-9999");
        adminUser.setAddress("Admin Address");
        adminUser.setRole(User.UserRole.ADMIN);
        adminUser.setEnabled(true);
        userRepository.save(adminUser);

        System.out.println("Sample users created successfully!");

        System.out.println("Adding categories...");
        
        // Create categories
        Category electronics = new Category();
        electronics.setName("Electronics");
        electronics.setDescription("Latest electronic devices and gadgets");
        electronics.setImageUrl("https://images.unsplash.com/photo-1498049794561-7780e7231661?w=300&h=300&fit=crop");
        categoryRepository.save(electronics);

        Category clothing = new Category();
        clothing.setName("Clothing");
        clothing.setDescription("Fashion and apparel for all seasons");
        clothing.setImageUrl("https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=300&h=300&fit=crop");
        categoryRepository.save(clothing);

        Category home = new Category();
        home.setName("Home & Garden");
        home.setDescription("Home improvement and garden supplies");
        home.setImageUrl("https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=300&h=300&fit=crop");
        categoryRepository.save(home);

        Category sports = new Category();
        sports.setName("Sports & Outdoors");
        sports.setDescription("Sports equipment and outdoor gear");
        sports.setImageUrl("https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=300&h=300&fit=crop");
        categoryRepository.save(sports);

        Category books = new Category();
        books.setName("Books & Media");
        books.setDescription("Books, movies, and entertainment");
        books.setImageUrl("https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=300&h=300&fit=crop");
        categoryRepository.save(books);

        Category beauty = new Category();
        beauty.setName("Beauty & Health");
        beauty.setDescription("Beauty products and health supplements");
        beauty.setImageUrl("https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?w=300&h=300&fit=crop");
        categoryRepository.save(beauty);

        System.out.println("Adding sample products...");

        // Electronics Category
        Product product1 = new Product();
        product1.setName("iPhone 15 Pro");
        product1.setDescription("Latest iPhone with A17 Pro chip, titanium design, and advanced camera system");
        product1.setPrice(BigDecimal.valueOf(999.99));
        product1.setStock(50);
        product1.setImageUrl("https://images.unsplash.com/photo-1592750475338-74b7b21085ab?w=300&h=300&fit=crop");
        product1.setCategory(electronics);
        product1.setBrand("Apple");
        product1.setModel("iPhone 15 Pro");
        product1.setRating(BigDecimal.valueOf(4.8));
        product1.setReviewCount(1250);
        productRepository.save(product1);

        Product product2 = new Product();
        product2.setName("MacBook Air M2");
        product2.setDescription("Ultra-thin laptop with M2 chip, 13.6-inch Liquid Retina display");
        product2.setPrice(BigDecimal.valueOf(1199.99));
        product2.setStock(30);
        product2.setImageUrl("https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=300&h=300&fit=crop");
        product2.setCategory(electronics);
        product2.setBrand("Apple");
        product2.setModel("MacBook Air M2");
        product2.setRating(BigDecimal.valueOf(4.9));
        product2.setReviewCount(890);
        productRepository.save(product2);

        Product product3 = new Product();
        product3.setName("Sony WH-1000XM5");
        product3.setDescription("Premium noise-canceling wireless headphones with 30-hour battery life");
        product3.setPrice(BigDecimal.valueOf(399.99));
        product3.setStock(100);
        product3.setImageUrl("https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=300&h=300&fit=crop");
        product3.setCategory(electronics);
        product3.setBrand("Sony");
        product3.setModel("WH-1000XM5");
        product3.setRating(BigDecimal.valueOf(4.7));
        product3.setReviewCount(567);
        productRepository.save(product3);

        Product product4 = new Product();
        product4.setName("Samsung 65\" QLED 4K TV");
        product4.setDescription("65-inch QLED 4K Smart TV with Quantum HDR and Alexa Built-in");
        product4.setPrice(BigDecimal.valueOf(1499.99));
        product4.setStock(25);
        product4.setImageUrl("https://images.unsplash.com/photo-1593359677879-a4bb92f829d1?w=300&h=300&fit=crop");
        product4.setCategory(electronics);
        product4.setBrand("Samsung");
        product4.setModel("QN65Q80TAFXZA");
        product4.setRating(BigDecimal.valueOf(4.6));
        product4.setReviewCount(432);
        productRepository.save(product4);

        // Clothing Category
        Product product5 = new Product();
        product5.setName("Nike Air Max 270");
        product5.setDescription("Comfortable running shoes with Air Max technology and breathable mesh");
        product5.setPrice(BigDecimal.valueOf(129.99));
        product5.setStock(200);
        product5.setImageUrl("https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=300&h=300&fit=crop");
        product5.setCategory(clothing);
        product5.setBrand("Nike");
        product5.setModel("Air Max 270");
        product5.setRating(BigDecimal.valueOf(4.5));
        product5.setReviewCount(890);
        productRepository.save(product5);

        Product product6 = new Product();
        product6.setName("Levi's 501 Original Jeans");
        product6.setDescription("Classic straight-leg jeans in authentic denim with button fly");
        product6.setPrice(BigDecimal.valueOf(89.99));
        product6.setStock(150);
        product6.setImageUrl("https://images.unsplash.com/photo-1542272604-787c3835535d?w=300&h=300&fit=crop");
        product6.setCategory(clothing);
        product6.setBrand("Levi's");
        product6.setModel("501 Original");
        product6.setRating(BigDecimal.valueOf(4.4));
        product6.setReviewCount(567);
        productRepository.save(product6);

        Product product7 = new Product();
        product7.setName("Adidas Ultraboost 22");
        product7.setDescription("Premium running shoes with responsive Boost midsole and Primeknit upper");
        product7.setPrice(BigDecimal.valueOf(179.99));
        product7.setStock(80);
        product7.setImageUrl("https://images.unsplash.com/photo-1608231387042-66d1773070a5?w=300&h=300&fit=crop");
        product7.setCategory(clothing);
        product7.setBrand("Adidas");
        product7.setModel("Ultraboost 22");
        product7.setRating(BigDecimal.valueOf(4.7));
        product7.setReviewCount(345);
        productRepository.save(product7);

        Product product8 = new Product();
        product8.setName("H&M Cotton T-Shirt");
        product8.setDescription("Comfortable cotton t-shirt in various colors and sizes");
        product8.setPrice(BigDecimal.valueOf(19.99));
        product8.setStock(500);
        product8.setImageUrl("https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=300&h=300&fit=crop");
        product8.setCategory(clothing);
        product8.setBrand("H&M");
        product8.setModel("Cotton T-Shirt");
        product8.setRating(BigDecimal.valueOf(4.2));
        product8.setReviewCount(1200);
        productRepository.save(product8);

        // Home & Garden Category
        Product product9 = new Product();
        product9.setName("IKEA MALM Bed Frame");
        product9.setDescription("Modern bed frame with headboard in white finish");
        product9.setPrice(BigDecimal.valueOf(299.99));
        product9.setStock(40);
        product9.setImageUrl("https://images.unsplash.com/photo-1505693314120-0d443867891c?w=300&h=300&fit=crop");
        product9.setCategory(home);
        product9.setBrand("IKEA");
        product9.setModel("MALM");
        product9.setRating(BigDecimal.valueOf(4.3));
        product9.setReviewCount(234);
        productRepository.save(product9);

        Product product10 = new Product();
        product10.setName("Philips Hue Smart Bulb");
        product10.setDescription("Smart LED bulb with 16 million colors and voice control");
        product10.setPrice(BigDecimal.valueOf(49.99));
        product10.setStock(300);
        product10.setImageUrl("https://images.unsplash.com/photo-1507473885765-e6ed057f782c?w=300&h=300&fit=crop");
        product10.setCategory(home);
        product10.setBrand("Philips");
        product10.setModel("Hue White and Color");
        product10.setRating(BigDecimal.valueOf(4.6));
        product10.setReviewCount(678);
        productRepository.save(product10);

        Product product11 = new Product();
        product11.setName("KitchenAid Stand Mixer");
        product11.setDescription("Professional stand mixer with 10-speed motor and tilt-head design");
        product11.setPrice(BigDecimal.valueOf(399.99));
        product11.setStock(60);
        product11.setImageUrl("https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=300&h=300&fit=crop");
        product11.setCategory(home);
        product11.setBrand("KitchenAid");
        product11.setModel("Artisan Series");
        product11.setRating(BigDecimal.valueOf(4.8));
        product11.setReviewCount(456);
        productRepository.save(product11);

        Product product12 = new Product();
        product12.setName("Dyson V15 Detect");
        product12.setDescription("Cordless vacuum with laser dust detection and 60-minute runtime");
        product12.setPrice(BigDecimal.valueOf(699.99));
        product12.setStock(35);
        product12.setImageUrl("https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=300&h=300&fit=crop");
        product12.setCategory(home);
        product12.setBrand("Dyson");
        product12.setModel("V15 Detect");
        product12.setRating(BigDecimal.valueOf(4.9));
        product12.setReviewCount(289);
        productRepository.save(product12);

        // Sports & Outdoors Category
        Product product13 = new Product();
        product13.setName("Fitbit Charge 5");
        product13.setDescription("Advanced fitness tracker with heart rate monitoring and GPS");
        product13.setPrice(BigDecimal.valueOf(179.99));
        product13.setStock(120);
        product13.setImageUrl("https://images.unsplash.com/photo-1575311373937-040b8e1fd5b6?w=300&h=300&fit=crop");
        product13.setCategory(sports);
        product13.setBrand("Fitbit");
        product13.setModel("Charge 5");
        product13.setRating(BigDecimal.valueOf(4.4));
        product13.setReviewCount(567);
        productRepository.save(product13);

        Product product14 = new Product();
        product14.setName("Yeti Rambler Tumbler");
        product14.setDescription("Insulated tumbler that keeps drinks cold for 24 hours");
        product14.setPrice(BigDecimal.valueOf(39.99));
        product14.setStock(400);
        product14.setImageUrl("https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=300&h=300&fit=crop");
        product14.setCategory(sports);
        product14.setBrand("Yeti");
        product14.setModel("Rambler 20oz");
        product14.setRating(BigDecimal.valueOf(4.7));
        product14.setReviewCount(890);
        productRepository.save(product14);

        Product product15 = new Product();
        product15.setName("Coleman Sundome Tent");
        product15.setDescription("4-person camping tent with weather protection and easy setup");
        product15.setPrice(BigDecimal.valueOf(89.99));
        product15.setStock(75);
        product15.setImageUrl("https://images.unsplash.com/photo-1523987355523-c7b5b0dd90a7?w=300&h=300&fit=crop");
        product15.setCategory(sports);
        product15.setBrand("Coleman");
        product15.setModel("Sundome 4-Person");
        product15.setRating(BigDecimal.valueOf(4.3));
        product15.setReviewCount(234);
        productRepository.save(product15);

        // Books & Media Category
        Product product16 = new Product();
        product16.setName("Kindle Paperwhite");
        product16.setDescription("Waterproof e-reader with 8GB storage and adjustable backlight");
        product16.setPrice(BigDecimal.valueOf(139.99));
        product16.setStock(200);
        product16.setImageUrl("https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=300&h=300&fit=crop");
        product16.setCategory(books);
        product16.setBrand("Amazon");
        product16.setModel("Kindle Paperwhite");
        product16.setRating(BigDecimal.valueOf(4.6));
        product16.setReviewCount(789);
        productRepository.save(product16);

        Product product17 = new Product();
        product17.setName("Sony WH-1000XM4");
        product17.setDescription("Wireless noise-canceling headphones with 30-hour battery life");
        product17.setPrice(BigDecimal.valueOf(349.99));
        product17.setStock(90);
        product17.setImageUrl("https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=300&h=300&fit=crop");
        product17.setCategory(books);
        product17.setBrand("Sony");
        product17.setModel("WH-1000XM4");
        product17.setRating(BigDecimal.valueOf(4.8));
        product17.setReviewCount(456);
        productRepository.save(product17);

        // Beauty & Health Category
        Product product18 = new Product();
        product18.setName("Dyson Airwrap");
        product18.setDescription("Multi-styler for curling, smoothing, and volumizing hair");
        product18.setPrice(BigDecimal.valueOf(599.99));
        product18.setStock(45);
        product18.setImageUrl("https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?w=300&h=300&fit=crop");
        product18.setCategory(beauty);
        product18.setBrand("Dyson");
        product18.setModel("Airwrap Multi-styler");
        product18.setRating(BigDecimal.valueOf(4.7));
        product18.setReviewCount(234);
        productRepository.save(product18);

        Product product19 = new Product();
        product19.setName("Oral-B iO Series 9");
        product19.setDescription("Smart electric toothbrush with AI technology and app connectivity");
        product19.setPrice(BigDecimal.valueOf(199.99));
        product19.setStock(150);
        product19.setImageUrl("https://images.unsplash.com/photo-1559591935-c6cc6b9a3f8a?w=300&h=300&fit=crop");
        product19.setCategory(beauty);
        product19.setBrand("Oral-B");
        product19.setModel("iO Series 9");
        product19.setRating(BigDecimal.valueOf(4.5));
        product19.setReviewCount(345);
        productRepository.save(product19);

        Product product20 = new Product();
        product20.setName("L'Oreal Paris Skincare Set");
        product20.setDescription("Complete skincare routine with cleanser, toner, and moisturizer");
        product20.setPrice(BigDecimal.valueOf(49.99));
        product20.setStock(300);
        product20.setImageUrl("https://images.unsplash.com/photo-1556228720-195a672e8a03?w=300&h=300&fit=crop");
        product20.setCategory(beauty);
        product20.setBrand("L'Oreal Paris");
        product20.setModel("Skincare Set");
        product20.setRating(BigDecimal.valueOf(4.3));
        product20.setReviewCount(567);
        productRepository.save(product20);

        System.out.println("Sample products created successfully!");

        // Create sample reviews
        createSampleReviews(product1, user1);
        createSampleReviews(product2, user2);
        createSampleReviews(product3, user3);
        createSampleReviews(product4, user4);
        createSampleReviews(product5, user5);

        System.out.println("Sample reviews created successfully!");
        System.out.println("Data initialization completed!");
    }

    private void createSampleReviews(Product product, User user) {
        // Create 3-5 reviews for each product
        String[] reviewTexts = {
            "Great product! Highly recommend.",
            "Excellent quality and fast shipping.",
            "Good value for money.",
            "Perfect for my needs.",
            "Amazing product, will buy again!"
        };

        BigDecimal[] ratings = {
            BigDecimal.valueOf(4.0),
            BigDecimal.valueOf(5.0),
            BigDecimal.valueOf(4.0),
            BigDecimal.valueOf(5.0),
            BigDecimal.valueOf(4.0)
        };

        for (int i = 0; i < 3; i++) {
            Review review = new Review();
            review.setProduct(product);
            review.setUser(user);
            review.setReviewerName(user.getFirstName() + " " + user.getLastName());
            review.setReviewerEmail(user.getEmail());
            review.setRating(ratings[i]);
            review.setComment(reviewTexts[i]);
            review.setCreatedAt(LocalDateTime.now().minusDays(i + 1));
            reviewRepository.save(review);
        }
    }
} 