package com.fooddelivery.common.config;

import com.fooddelivery.common.enums.Role;
import com.fooddelivery.user.entity.User;
import com.fooddelivery.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            log.info("Loading sample data...");
            
            // Create Admin user
            User admin = User.builder()
                    .email("admin@fooddelivery.com")
                    .password(passwordEncoder.encode("admin123"))
                    .firstName("Admin")
                    .lastName("User")
                    .phone("9999999999")
                    .role(Role.ADMIN)
                    .active(true)
                    .build();
            admin.getRoles().add(Role.ADMIN);
            userRepository.save(admin);
            log.info("Created admin user: admin@fooddelivery.com / admin123");
            
            // Create Customer user
            User customer = User.builder()
                    .email("customer@fooddelivery.com")
                    .password(passwordEncoder.encode("customer123"))
                    .firstName("John")
                    .lastName("Doe")
                    .phone("9876543210")
                    .role(Role.CUSTOMER)
                    .active(true)
                    .address("123 Main St")
                    .city("Mumbai")
                    .pincode("400001")
                    .build();
            customer.getRoles().add(Role.CUSTOMER);
            userRepository.save(customer);
            log.info("Created customer user: customer@fooddelivery.com / customer123");
            
            // Create Restaurant Owner user
            User owner = User.builder()
                    .email("owner@fooddelivery.com")
                    .password(passwordEncoder.encode("owner123"))
                    .firstName("Restaurant")
                    .lastName("Owner")
                    .phone("9876543211")
                    .role(Role.RESTAURANT_OWNER)
                    .active(true)
                    .build();
            owner.getRoles().add(Role.RESTAURANT_OWNER);
            userRepository.save(owner);
            log.info("Created restaurant owner user: owner@fooddelivery.com / owner123");
            
            // Create Delivery Partner user
            User deliveryPartner = User.builder()
                    .email("delivery@fooddelivery.com")
                    .password(passwordEncoder.encode("delivery123"))
                    .firstName("Delivery")
                    .lastName("Partner")
                    .phone("9876543212")
                    .role(Role.DELIVERY_PARTNER)
                    .active(true)
                    .build();
            deliveryPartner.getRoles().add(Role.DELIVERY_PARTNER);
            userRepository.save(deliveryPartner);
            log.info("Created delivery partner user: delivery@fooddelivery.com / delivery123");
            
            log.info("Sample data loaded successfully!");
        }
    }
}
