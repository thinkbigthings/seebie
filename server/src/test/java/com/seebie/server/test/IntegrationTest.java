package com.seebie.server.test;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.seebie.server.PropertyLogger;
import com.seebie.server.service.SleepService;
import com.seebie.server.service.UserService;
import com.seebie.server.test.data.TestDataPopulator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Arrays;

import static com.github.dockerjava.api.model.Ports.Binding.bindPort;


@Tag("integration")
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "logging.level.org.hibernate.SQL=DEBUG",
        "logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE",
        "spring.main.lazy-initialization=true",
        "spring.flyway.enabled=true",
        "app.notification.scan.enabled=false",
        "app.security.rememberMe.tokenValidity=6s",
        "spring.session.timeout=4s",
        "app.security.rememberMe.key=test-only",
        "spring.mail.username=test-only"
        })
public class IntegrationTest {

    @TestConfiguration
    public static class TestConfig {

        @Bean public MailSender createMailSenderToLogs() {
            LOG.info("Using a mail sender that logs to the console instead of sending emails.");
            return new MailSenderToLogs();
        }

        @Bean public PropertyLogger createPropertyLogger(ConfigurableEnvironment env) {
            LOG.info("Log all properties for debugging, including sensitive values, so don't use in production");
            return new PropertyLogger(env);
        }

        @Bean
        public FlywayMigrationStrategy clean() {
            LOG.info("Cleaning database for integration tests so we don't have state from previous runs");
            return flyway -> {
                flyway.clean();
                flyway.migrate();
            };
        }

        @Bean
        public TestDataPopulator createTestDataPopulator(UserService userService, SleepService sleepService) {
            LOG.info("Populating test data for integration tests.");
            return new TestDataPopulator(userService, sleepService);
        }
    }

    private static final Logger LOG = LoggerFactory.getLogger(IntegrationTest.class);
    private static final String POSTGRES_IMAGE = "postgres:15.4";
    private static final int PG_PORT = 5432;

    // can only set to false if other instances are shut down first
    private static final boolean leaveRunningAfterTests = true;

    protected static PostgreSQLContainer<?> postgres;

    @BeforeAll
    static void setupDatabase() {

        var hostConfig = new HostConfig().withPortBindings(new PortBinding(bindPort(PG_PORT), new ExposedPort(PG_PORT)));
        postgres = new PostgreSQLContainer<>(POSTGRES_IMAGE)
                .withReuse(leaveRunningAfterTests)
                .withUsername("test")
                .withPassword("test")
                .withCreateContainerCmdModifier(cmd -> cmd.withHostConfig(hostConfig));

        // call start ourselves so we can reuse
        // instead of letting library manage it with @TestContainers and @Container
        postgres.start();
    }

    @BeforeEach
    public void startup(TestInfo testInfo) {

        LOG.info("");
        LOG.info("=======================================================================================");
        LOG.info("Executing test " + testInfo.getDisplayName());
        LOG.info("TestContainer username: " + postgres.getUsername());
        LOG.info("TestContainer password: " + postgres.getPassword());
        LOG.info("TestContainer jdbc url: " + postgres.getJdbcUrl());
        LOG.info("");
    }

    public static class MailSenderToLogs implements MailSender {

        private static Logger LOG = LoggerFactory.getLogger(MailSenderToLogs.class);

        @Override
        public void send(SimpleMailMessage message) throws MailException {
            LOG.info("Sending email: " + message);
        }

        @Override
        public void send(SimpleMailMessage... messages) throws MailException {
            Arrays.stream(messages).forEach(this::send);
        }
    }
}
