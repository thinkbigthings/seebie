package com.seebie.server.controller;

import com.seebie.server.dto.PersonalInfo;
import com.seebie.server.dto.RegistrationRequest;
import com.seebie.server.dto.User;
import com.seebie.server.dto.UserSummary;
import com.seebie.server.service.UserService;
import com.seebie.server.test.IntegrationTest;
import com.seebie.server.test.client.ParsablePage;
import com.seebie.server.test.client.RestClientFactory;
import com.seebie.server.test.data.TestData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.web.client.RestClient;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This is here mainly to flex the server through a live http connection.
 */
public class EndToEndIntegrationTest extends IntegrationTest {

    protected static Logger LOG = LoggerFactory.getLogger(EndToEndIntegrationTest.class);

    private static URI usersUrl;

    private static String testUserName;
    private static String testUserPassword;
    private static URI testUserUrl;
    private static URI testUserUpdatePasswordUrl;

    private static RestClientFactory clientFactory;

    @BeforeAll
    public static void createTestData(@Autowired RestClient.Builder builder, @Autowired UserService userService) {

        LOG.info("");
        LOG.info("=======================================================================================");
        LOG.info("Creating test data");
        LOG.info("");

        // we get the rest client builder as configured for the app, including mappers
        clientFactory = new RestClientFactory(builder, baseUribuilder.builder().build());

        RegistrationRequest testUserRegistration = TestData.createRandomUserRegistration();
        userService.saveNewUser(testUserRegistration);

        testUserName = testUserRegistration.username();
        testUserPassword = testUserRegistration.plainTextPassword();

        var userUriBuilder = baseUribuilder.builder().pathSegment("api", "user");

        usersUrl = userUriBuilder.build();
        testUserUrl = userUriBuilder.pathSegment(testUserName).build();
        testUserUpdatePasswordUrl = userUriBuilder.pathSegment("password", "update").build();
    }

    @Test()
    @DisplayName("Admin list users")
    public void adminListUsers() {

        RestClient admin = clientFactory.login("admin", "admin");

        // anonymous inner classes should generally be avoided due to risk of memory leaks,
        // but this is a test and we're not going to be creating a lot of these before the JVM exits
        var userPage = new ParameterizedTypeReference<ParsablePage<UserSummary>>() {};
        Page<UserSummary> page = admin.get().uri(usersUrl).retrieve().body(userPage);

        assertTrue(page.isFirst());
        assertTrue(page.getTotalElements() >= 1);
    }

    @Test
    @DisplayName("Update user password")
    public void testUpdatePassword() {

        RestClient user = clientFactory.login(testUserName, testUserPassword);
        PersonalInfo info = user.get().uri(testUserUrl).retrieve().body(User.class).personalInfo();

        String newPassword = testUserPassword + "1";
        user.post().uri(testUserUpdatePasswordUrl).body(newPassword).retrieve();

        user = clientFactory.login(testUserName, newPassword);
        PersonalInfo info2 = user.get().uri(testUserUrl).retrieve().body(User.class).personalInfo();

        assertEquals(info, info2);
    }
}
