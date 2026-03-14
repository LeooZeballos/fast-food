package net.leozeballos.FastFood.config;

import lombok.RequiredArgsConstructor;
import net.leozeballos.FastFood.auth.User;
import net.leozeballos.FastFood.auth.UserRepository;
import net.leozeballos.FastFood.branch.Branch;
import net.leozeballos.FastFood.branch.BranchRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * DataInitializer now only handles User setup.
 * All Products, Menus, and Branches are managed via Flyway SQL Migrations.
 */
@Configuration
@Profile("dev")
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final BranchRepository branchRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void run(String... args) {
        // Only initialize users if they don't exist
        if (userRepository.count() == 0) {
            List<Branch> allBranches = branchRepository.findAll();
            if (allBranches.isEmpty()) {
                System.out.println("⚠️ Cannot initialize users: No branches found. Check Flyway migrations.");
                return;
            }

            Branch b1 = allBranches.get(0);

            // Admin: Global Access
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword("$2a$10$yafJV01hrbBMWhcHU4pqCeOjT9czyBtLQsdaTN14noy7VyTuPBBQS"); // "admin"
            admin.setRoles(new HashSet<>(Set.of("ADMIN")));
            admin.setEnabled(true);
            admin.setBranch(b1);
            userRepository.save(admin);

            // Staff 1
            User staff1 = new User();
            staff1.setUsername("staff1");
            staff1.setPassword("$2a$10$yafJV01hrbBMWhcHU4pqCeOjT9czyBtLQsdaTN14noy7VyTuPBBQS");
            staff1.setRoles(new HashSet<>(Set.of("USER")));
            staff1.setEnabled(true);
            staff1.setBranch(b1);
            userRepository.save(staff1);

            System.out.println("--- Users Initialized successfully ---");
        }
    }
}
