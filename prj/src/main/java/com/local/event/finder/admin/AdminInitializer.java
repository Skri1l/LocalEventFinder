package com.local.event.finder.admin;

import com.local.event.finder.logging.AppLogger;
import com.local.event.finder.logging.LoggerFactory;
import com.local.event.finder.user.User;
import com.local.event.finder.user.UserController;
import com.local.event.finder.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final static AppLogger log = LoggerFactory.getLogger(AdminInitializer.class);

    @Override
    public void run(String... args) throws Exception {
        boolean adminExist = userRepository.existsByRole("ADMIN");

        if (!adminExist) {
            User admin = new User();

            admin.setEmail("admin@gmail.com");
            admin.setUsername("admin");
            admin.setPasswordHash(passwordEncoder.encode("admin"));
            admin.setRole("ADMIN");
            admin.setBlocked(false);

            userRepository.save(admin);
            log.info("Admin has been created");
        }
    }
}
