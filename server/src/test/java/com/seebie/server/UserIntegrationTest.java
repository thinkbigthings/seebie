package com.seebie.server;

import com.seebie.server.service.UserService;
import com.seebie.server.test.IntegrationTest;
import com.seebie.server.test.client.ApiClientStateful;
import com.seebie.server.test.data.TestData;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import com.seebie.server.dto.PersonalInfo;
import com.seebie.server.dto.RegistrationRequest;
import com.seebie.server.dto.User;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;


public class UserIntegrationTest extends IntegrationTest {

    protected static Logger LOG = LoggerFactory.getLogger(UserIntegrationTest.class);

    private static String baseUrl;
    private static URI users;

    private static String testUserName;
    private static String testUserPassword;
    private static URI testUserUrl;
    private static URI testUserUpdatePasswordUrl;

    private static ApiClientStateful userClient;

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

        RegistrationRequest testUserRegistration = TestData.createRandomUserRegistration();
        userService.saveNewUser(testUserRegistration);

        testUserName = testUserRegistration.username();
        testUserPassword = testUserRegistration.plainTextPassword();
        testUserUrl = URI.create(users + "/" + testUserName);
        testUserUpdatePasswordUrl = URI.create(testUserUrl + "/password/update");

        userClient = new ApiClientStateful(baseUrl, testUserName, testUserPassword);
    }

    @Test
    @DisplayName("Update user password")
    public void testUpdatePassword() {

        PersonalInfo info = userClient.get(testUserUrl, User.class).personalInfo();
        String newPassword = "password";
        userClient.post(testUserUpdatePasswordUrl, newPassword);
        userClient = new ApiClientStateful(baseUrl, testUserName, newPassword);

        PersonalInfo info2 = userClient.get(testUserUrl, User.class).personalInfo();
        assertEquals(info, info2);
    }

    @Test
    @DisplayName("Update user info")
    public void updateUserInfo() {

        var updatedInfo = TestData.createRandomPersonalInfo();
        var savedInfo = userService.updateUser(testUserName, updatedInfo).personalInfo();
        assertEquals(updatedInfo, savedInfo);
    }

}
