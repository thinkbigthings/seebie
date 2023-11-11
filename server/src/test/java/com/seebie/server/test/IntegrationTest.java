package com.seebie.server.test;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
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
        "app.security.rememberMe.key=0ef16205-ba16-4154-b843-8bd1709b1ef4",
        })
public class IntegrationTest {

    @TestConfiguration
    public static class EmailTestConfig {
        @Bean
        public MailSender createMailSenderToLogs() {
            return new MailSenderToLogs();
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
