package com.seebie.server.controller;

import com.seebie.server.dto.*;
import com.seebie.server.service.UserService;
import com.seebie.server.test.IntegrationTest;
import com.seebie.server.test.client.RestClientFactory;
import com.seebie.server.test.data.TestData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestClient;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * This is here mainly to flex the server through a live http connection.
 */
public class EndToEndIntegrationTest extends IntegrationTest {

    protected static Logger LOG = LoggerFactory.getLogger(EndToEndIntegrationTest.class);

    private static String testUserEmail;
    private static String testUserPassword;
    private static String testUserPublicId;
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

        testUserEmail = testUserRegistration.email();
        testUserPublicId = userService.getUserByEmail(testUserEmail).publicId();
        testUserPassword = testUserRegistration.plainTextPassword();

        var userUriBuilder = baseUribuilder.builder().pathSegment("api", "user");

        testUserUrl = userUriBuilder.pathSegment(testUserPublicId).build();
        testUserUpdatePasswordUrl = userUriBuilder.pathSegment("password", "update").build();
    }

    @Test
    @DisplayName("Update user password")
    public void testUpdatePassword() {

        RestClient user = clientFactory.login(testUserEmail, testUserPassword);

        PersonalInfo info = user.get().uri(testUserUrl).retrieve().body(User.class).personalInfo();

        PasswordResetRequest pwReset = new PasswordResetRequest(testUserPassword + "1");
        user.post().uri(testUserUpdatePasswordUrl).body(pwReset).retrieve().body(String.class);

        user = clientFactory.login(testUserEmail, pwReset.plainTextPassword());
        PersonalInfo info2 = user.get().uri(testUserUrl).retrieve().body(User.class).personalInfo();

        assertEquals(info, info2);
    }
}
