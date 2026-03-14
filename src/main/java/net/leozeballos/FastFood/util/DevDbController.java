package net.leozeballos.FastFood.util;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dev/db")
@Profile("dev")
@RequiredArgsConstructor
public class DevDbController {

    private final JdbcTemplate jdbcTemplate;

    @PostMapping("/reset")
    public String resetDb() {
        try {
            jdbcTemplate.execute("DROP TABLE IF EXISTS flyway_schema_history CASCADE");
            jdbcTemplate.execute("DROP TABLE IF EXISTS inventory CASCADE");
            jdbcTemplate.execute("DROP TABLE IF EXISTS menu_item CASCADE");
            jdbcTemplate.execute("DROP TABLE IF EXISTS food_order_food_order_details CASCADE");
            jdbcTemplate.execute("DROP TABLE IF EXISTS food_order_detail CASCADE");
            jdbcTemplate.execute("DROP TABLE IF EXISTS food_order CASCADE");
            jdbcTemplate.execute("DROP TABLE IF EXISTS item CASCADE");
            jdbcTemplate.execute("DROP TABLE IF EXISTS branch CASCADE");
            jdbcTemplate.execute("DROP TABLE IF EXISTS address CASCADE");
            jdbcTemplate.execute("DROP TABLE IF EXISTS user_roles CASCADE");
            jdbcTemplate.execute("DROP TABLE IF EXISTS users CASCADE");
            return "Database schema nuked successfully. Restart the application to trigger a fresh Flyway migration.";
        } catch (Exception e) {
            return "Failed to nuke database: " + e.getMessage();
        }
    }
}
