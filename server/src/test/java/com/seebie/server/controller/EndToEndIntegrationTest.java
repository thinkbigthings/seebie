package com.seebie.server.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seebie.server.dto.PersonalInfo;
import com.seebie.server.dto.RegistrationRequest;
import com.seebie.server.dto.User;
import com.seebie.server.dto.UserSummary;
import com.seebie.server.service.UserService;
import com.seebie.server.test.IntegrationTest;
import com.seebie.server.test.client.ApiClientStateful;
import com.seebie.server.test.client.ParsablePage;
import com.seebie.server.test.data.TestData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.data.domain.Page;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This is here mainly to flex the server through a live http connection.
 */
public class EndToEndIntegrationTest extends IntegrationTest {

    protected static Logger LOG = LoggerFactory.getLogger(EndToEndIntegrationTest.class);

    private static String baseUrl;

    private static URI users;

    private static String testUserName;
    private static String testUserPassword;
    private static URI testUserUrl;
    private static URI testUserUpdatePasswordUrl;

    private ObjectMapper mapper;

    private static ApiClientStateful adminClient;
    private static ApiClientStateful userClient;

    @BeforeAll
    public static void setupClient(@Autowired RestClient.Builder restClientBuilder, @Autowired SslBundles sslBundles) {

        LOG.info("SSL Bundle: ");
        LOG.info(sslBundles.getBundle("mybundle").toString());

//        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
//                .withReadTimeout(Duration.ofMinutes(2))
//                .withSslBundle(sslBundles.getBundle("mybundle"));
//
//        ClientHttpRequestFactory requestFactory = ClientHttpRequestFactories.get(settings);
//
//        var restClient = restClientBuilder.baseUrl("https://example.org")
//                .
//                                            .requestFactory(requestFactory)
//                                            .build();

    }

    @BeforeEach
    public void setup(@Autowired MappingJackson2HttpMessageConverter converter) {
        // use the actual mapper configured in the application
        mapper = converter.getObjectMapper();
    }

    @BeforeAll
    public static void createTestData(@Autowired UserService userService, @LocalServerPort int randomServerPort) {

        LOG.info("");
        LOG.info("=======================================================================================");
        LOG.info("Creating test data");
        LOG.info("");

        baseUrl = STR."https://localhost:\{randomServerPort}/api/";
        users = URI.create(baseUrl + "user");

        adminClient = new ApiClientStateful(baseUrl, "admin", "admin");


        RegistrationRequest testUserRegistration = TestData.createRandomUserRegistration();
        userService.saveNewUser(testUserRegistration);

        testUserName = testUserRegistration.username();
        testUserPassword = testUserRegistration.plainTextPassword();
        testUserUrl = URI.create(users + "/" + testUserName);
        testUserUpdatePasswordUrl = URI.create(testUserUrl + "/password/update");

        userClient = new ApiClientStateful(baseUrl, testUserName, testUserPassword);
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
    @DisplayName("Update user password")
    public void testUpdatePassword() {

        PersonalInfo info = userClient.get(testUserUrl, User.class).personalInfo();
        String newPassword = "password";
        userClient.post(testUserUpdatePasswordUrl, newPassword);
        userClient = new ApiClientStateful(baseUrl, testUserName, newPassword);

        PersonalInfo info2 = userClient.get(testUserUrl, User.class).personalInfo();
        assertEquals(info, info2);
    }
}
