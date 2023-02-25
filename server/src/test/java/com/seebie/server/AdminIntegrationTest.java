package com.seebie.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seebie.server.dto.PersonalInfo;
import com.seebie.server.dto.User;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.domain.Page;
import com.seebie.server.dto.UserSummary;
import com.seebie.server.service.UserService;
import com.seebie.server.test.client.ApiClientStateful;
import com.seebie.server.test.client.ParsablePage;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

public class AdminIntegrationTest extends IntegrationTest {

    protected static Logger LOG = LoggerFactory.getLogger(AdminIntegrationTest.class);

    private static String baseUrl;
    private static URI users;
    private static URI adminUser;

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
        adminUser = URI.create(users+"/admin");

        adminClient = new ApiClientStateful(baseUrl, "admin", "admin");
    }

    @Test()
    @DisplayName("Admin get user")
    public void adminGetUser() throws JsonProcessingException {

        PersonalInfo info  = adminClient.get(adminUser, User.class).personalInfo();

        assertTrue(info.toString().length() > 0);
    }

    @Test()
    @DisplayName("Admin list users")
    public void adminListUsers() throws JsonProcessingException {

        String results = adminClient.get(users);
        Page<UserSummary> page = mapper.readValue(results, new TypeReference<ParsablePage<UserSummary>>() {});

        assertTrue(page.isFirst());
        assertTrue(page.getTotalElements() >= 1);
    }
}
