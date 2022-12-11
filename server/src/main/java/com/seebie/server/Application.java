package com.seebie.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;

import jakarta.annotation.PreDestroy;

import static java.util.Arrays.asList;

@ConfigurationPropertiesScan
@SpringBootApplication
public class Application {

    private static Logger LOG = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {

        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);

        if(hasProfile(context, "migration")) {
            LOG.info("This is a migration-only profile, shutting down.");
            // call context close, otherwise something is making it wait 60 seconds
            // don't necessarily need System.exit, but that would also do the job immediately
            context.close();
        }
    }

    @PreDestroy
    protected static void preDestroy() {
        LOG.info("Application is exiting.");
    }

    private static boolean hasProfile(ConfigurableApplicationContext context, String profile) {
        return asList(context.getEnvironment().getActiveProfiles()).contains(profile);
    }

}
