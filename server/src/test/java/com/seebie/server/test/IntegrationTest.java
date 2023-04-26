package com.seebie.server.test;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.net.URI;
import java.net.http.HttpRequest;

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

    private static Logger LOG = LoggerFactory.getLogger(IntegrationTest.class);

    private static final String POSTGRES_IMAGE = "postgres:14";
    private static final int PG_PORT = 5432;

    // can only set to false if other instances are shut down first
    private static final boolean leaveRunningAfterTests = true;

    protected static PostgreSQLContainer<?> postgres;

    protected static String baseUrl;

    protected static URI loginUri;
    protected static URI rememberMeUri;

    protected static HttpRequest loginRequest;
    protected static HttpRequest loginRememberMe;

    @BeforeAll
    static void setupDatabase(@LocalServerPort int randomServerPort) {

        baseUrl = "https://localhost:" + randomServerPort;

        loginUri = URI.create(baseUrl + "/login?remember-me=false");
        rememberMeUri = URI.create(baseUrl + "/login?remember-me=true");

        loginRequest = HttpRequest.newBuilder().GET().uri(loginUri).build();
        loginRememberMe = HttpRequest.newBuilder().GET().uri(rememberMeUri).build();

        // need "autosave conservative" config, otherwise pg driver has caching issues with blue-green deployment
        // (org.postgresql.util.PSQLException: ERROR: cached plan must not change result type)

        var hostConfig = new HostConfig().withPortBindings(new PortBinding(bindPort(PG_PORT), new ExposedPort(PG_PORT)));
        postgres = new PostgreSQLContainer<>(POSTGRES_IMAGE)
                .withUrlParam("autosave", "conservative")
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
        LOG.info("using web server base " + baseUrl);
        LOG.info("TestContainer jdbc url: " + postgres.getJdbcUrl());
        LOG.info("TestContainer username: " + postgres.getUsername());
        LOG.info("TestContainer password: " + postgres.getPassword());
        LOG.info("");
    }

}
