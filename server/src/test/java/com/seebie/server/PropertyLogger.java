package com.seebie.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static java.util.function.Predicate.not;

/**
 * This class implements CommandLineRunner to ensure properties are logged after the
 * Spring context is fully initialized. Using CommandLineRunner guarantees that all
 * properties, including those set via command-line arguments, configuration files, and
 * environment variables, are loaded and available. Logging in a constructor is not
 * ideal as the Spring application context might not be fully initialized, leading to
 * incomplete data or issues with constructor-based dependency injection. CommandLineRunner
 * provides a reliable and predictable point in the application lifecycle, after context
 * loading but before the main loop begins, to perform such tasks.
 */
public class PropertyLogger implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(PropertyLogger.class);

    record PropertyDetails(String key, String value, String origin) {
        @Override public String toString() {
           return "Property name: " + key + " = " + value + " (Source: " + origin + ")";
        }
    }

    private final ConfigurableEnvironment env;

    public PropertyLogger(ConfigurableEnvironment env) {
        this.env = env;
    }

    @Override
    public void run(String... args) {

        env.getPropertySources().stream()
                .filter(not(source -> (source instanceof PropertySource.StubPropertySource))) // otherwise throws exceptions on getSource()
                .filter(source -> source.getSource() instanceof Map)
                .flatMap(source -> toPropertyDetails(source).stream())
                .sorted(Comparator.comparing(PropertyDetails::key))
                .forEach(property -> LOG.info(property.toString()));
    }

    private List<PropertyDetails> toPropertyDetails(PropertySource propertySource) {
        return ((Map<?,?>)propertySource.getSource()).entrySet().stream()
                .map(entry -> new PropertyDetails(entry.getKey().toString(), entry.getValue().toString(), propertySource.getName()))
                .toList();
    }

}
