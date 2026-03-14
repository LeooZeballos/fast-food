package net.leozeballos.FastFood.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.leozeballos.FastFood.auth.User;
import net.leozeballos.FastFood.auth.UserRepository;
import net.leozeballos.FastFood.branch.Branch;
import net.leozeballos.FastFood.branch.BranchRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * DataInitializer now only handles User setup.
 * All Products, Menus, and Branches are managed via Flyway SQL Migrations.
 */
@Slf4j
@Configuration
@Profile("dev")
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final BranchRepository branchRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.initial-password:#{null}}")
    private String adminInitialPassword;

    @Override
    @Transactional
    public void run(String... args) {
        // Only initialize users if they don't exist
        if (userRepository.count() == 0) {
            List<Branch> allBranches = branchRepository.findAll();
            if (allBranches.isEmpty()) {
                log.warn("⚠️ Cannot initialize users: No branches found. Check Flyway migrations.");
                return;
            }

            Branch b1 = allBranches.get(0);
            String initialPassword = resolveAdminPassword();

            // Admin: Global Access
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(initialPassword);
            admin.setRoles(new HashSet<>(Set.of("ADMIN")));
            admin.setEnabled(true);
            admin.setBranch(b1);
            userRepository.save(admin);

            // Staff 1
            User staff1 = new User();
            staff1.setUsername("staff1");
            staff1.setPassword(initialPassword); // Using same initial password for simplicity in dev
            staff1.setRoles(new HashSet<>(Set.of("USER")));
            staff1.setEnabled(true);
            staff1.setBranch(b1);
            userRepository.save(staff1);

            log.info("--- Users Initialized successfully ---");
        }
    }

    private String resolveAdminPassword() {
        if (adminInitialPassword != null && !adminInitialPassword.isBlank()) {
            log.info("Using configured ADMIN_INITIAL_PASSWORD from environment.");
            return passwordEncoder.encode(adminInitialPassword);
        }
        
        // Generate a random secure password on first run
        String generated = UUID.randomUUID().toString();
        log.warn("=======================================================");
        log.warn("  No ADMIN_INITIAL_PASSWORD set.");
        log.warn("  Generated initial password for users: {}", generated);
        log.warn("  Change this immediately after first login!");
        log.warn("=======================================================");
        return passwordEncoder.encode(generated);
    }
}
