package com.seebie.server.test;

import com.seebie.server.PropertyLogger;
import com.seebie.server.service.ChallengeService;
import com.seebie.server.service.SleepService;
import com.seebie.server.service.UserService;
import com.seebie.server.test.data.TestData;
import com.seebie.server.test.data.TestDataPopulator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.mail.MailSender;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.util.UUID;

@Tag("integration")
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "logging.level.org.hibernate.SQL=DEBUG",
        "logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE",
        "spring.main.lazy-initialization=true",
        "spring.flyway.enabled=true",
        "app.security.rememberMe.tokenValidity=2s", // small values for SessionSecurityTest
        "spring.session.timeout=1s", // small values for SessionSecurityTest
        "app.security.rememberMe.key=test-only",
        "spring.mail.username=test-only"
        })
public class IntegrationTest {

    @Autowired
    protected UserService userService;

    @TestConfiguration(proxyBeanMethods = false)
    @ImportTestcontainers(IntegrationTest.class) // import class containing @Container to work with bootTestRun
    public static class TestConfig {

        @Bean public PropertyLogger createPropertyLogger(ConfigurableEnvironment env) {
            return new PropertyLogger(env);
        }

        @Bean
        public TestDataPopulator createTestDataPopulator(UserService users, SleepService sleep, ChallengeService challenges) {
            return new TestDataPopulator(users, sleep, challenges);
        }

        @Bean public MailSender createMailSenderToLogs() {
            return new MailSenderToLogs();
        }
    }

    // Don't use @Testcontainers annotation, so we manage lifecycle instead of testcontainers managing it.
    // That way we can reuse for all tests. This has around a 20% performance savings for integration tests.
    // Note that if we let Testcontainers manage it, we need @DirtiesContext too.
    // Use @Container here, so it can be detected by @ImportTestcontainers
    @ServiceConnection
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.2");

    static {
        postgres.withUrlParam("autosave", "conservative");
        postgres.start();
    }

    protected static DefaultUriBuilderFactory baseUribuilder;

    @BeforeAll
    public static void setup(@LocalServerPort int randomServerPort) {
        baseUribuilder = new DefaultUriBuilderFactory("https://localhost:" + randomServerPort);
    }

    /**
     * This setup is so common it makes sense for IntegrationTest to hold the UserService.
     * @return
     */
    protected UUID saveNewUser() {
        var registration = TestData.createRandomUserRegistration();
        userService.saveNewUser(registration);
        return UUID.fromString(userService.getUserByEmail(registration.email()).publicId());
    }
}
