package com.seebie.server.test;

import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This cleans the database for the integration tests.
 * Otherwise when running the integration tests
 * the database would start out in the state it was left in from the previous run.
 */
@Configuration
public class FlywayTestConfig {

    @Bean
    public FlywayMigrationStrategy clean() {
        return flyway -> {
            flyway.clean();
            flyway.migrate();
        };
    }
}