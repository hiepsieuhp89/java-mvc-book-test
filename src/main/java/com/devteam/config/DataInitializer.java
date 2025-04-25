package com.devteam.config;

import com.devteam.dao.UserRepository;
import com.devteam.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Lazy
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        try {
            // Create admin user if it doesn't exist
            if (!userRepository.existsByUsername("admin")) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setEmail("admin@example.com");
                admin.setFullName("Quản trị viên");
                admin.setActive(true);
                admin.setEnabled(true);
                admin.addRole("ADMIN");
                admin.addRole("USER");
                
                userRepository.save(admin);
                
                System.out.println("Admin user created with username: admin and password: admin123");
            }
            
            // Create test user if it doesn't exist
            if (!userRepository.existsByUsername("user")) {
                User user = new User();
                user.setUsername("user");
                user.setPassword(passwordEncoder.encode("user123"));
                user.setEmail("user@example.com");
                user.setFullName("Người dùng test");
                user.setActive(true);
                user.setEnabled(true);
                user.addRole("USER");
                
                userRepository.save(user);
                
                System.out.println("Test user created with username: user and password: user123");
            }
        } catch (DataAccessException e) {
            System.err.println("Lỗi khi khởi tạo dữ liệu người dùng: " + e.getMessage());
            System.err.println("Bạn có thể cần cập nhật cấu trúc cơ sở dữ liệu của mình.");
            // Không throw exception để ứng dụng vẫn khởi động được
        }
    }
} 