package com.seebie.server.test;

import com.seebie.server.PropertyLogger;
import com.seebie.server.service.SleepService;
import com.seebie.server.service.UserService;
import com.seebie.server.test.data.TestDataPopulator;
import org.junit.jupiter.api.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Arrays;

@Tag("integration")
@DirtiesContext
@Testcontainers
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

    private static final Logger LOG = LoggerFactory.getLogger(IntegrationTest.class);

    @TestConfiguration(proxyBeanMethods = false)
    public static class TestConfig {

        @Bean public MailSender createMailSenderToLogs() {
            return new MailSenderToLogs();
        }

        @Bean public PropertyLogger createPropertyLogger(ConfigurableEnvironment env) {
            return new PropertyLogger(env);
        }

        @Bean
        public FlywayMigrationStrategy clean() {
            return flyway -> {
                flyway.clean();
                flyway.migrate();
            };
        }

        @Bean
        public TestDataPopulator createTestDataPopulator(UserService userService, SleepService sleepService) {
            return new TestDataPopulator(userService, sleepService);
        }

        @Bean
        @ServiceConnection
        public PostgreSQLContainer<?> postgresForBootTestRun() {
            return new PostgreSQLContainer<>("postgres:15.4").withDatabaseName("testconfig");
        }
    }

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgresForTests = new PostgreSQLContainer<>("postgres:15.4").withDatabaseName("inttest");


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
