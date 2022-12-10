package org.thinkbigthings.zdd.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.domain.Page;
import org.thinkbigthings.zdd.dto.UserSummary;
import org.thinkbigthings.zdd.server.service.UserService;
import org.thinkbigthings.zdd.server.test.client.ApiClientStateful;
import org.thinkbigthings.zdd.server.test.client.ParsablePage;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

public class AdminIntegrationTest extends IntegrationTest {

    protected static Logger LOG = LoggerFactory.getLogger(AdminIntegrationTest.class);

    private static String baseUrl;
    private static URI users;

    private ObjectMapper mapper = new ObjectMapper();

    private static ApiClientStateful adminClient;

    // this plugs in just a piece of the running app to our test code
    // we can use it for quickly bootstrapping test data without going through the API
    @Autowired
    private UserService userService;

    @BeforeAll
    public static void createTestData(@Autowired UserService userService, @LocalServerPort int randomServerPort) {

        LOG.info("");
        LOG.info("=======================================================================================");
        LOG.info("Creating test data");
        LOG.info("");

        baseUrl = "https://localhost:" + randomServerPort + "/";
        users = URI.create(baseUrl + "user");

        adminClient = new ApiClientStateful(baseUrl, "admin", "admin");
    }


    @Test()
    @DisplayName("Admin list users")
    public void adminListUsers() throws JsonProcessingException {

        String results = adminClient.get(users);
        Page<UserSummary> page = mapper.readValue(results, new TypeReference<ParsablePage<UserSummary>>() {});

        assertTrue(page.isFirst());
        assertTrue(page.getTotalElements() >= 1);
    }

    @Test
    @DisplayName("Health Check")
    public void testHealth() throws URISyntaxException, JsonProcessingException {

        ApiClientStateful adminClient = new ApiClientStateful(baseUrl, "admin", "admin");

        String healthResponse = adminClient.get(new URI(baseUrl + "actuator/health"));

        record Health(String status) {}
        Health healthMessage = mapper.readValue(healthResponse, Health.class);

        assertEquals("UP", healthMessage.status());
    }

}
