package com.seebie.server.controller;

import com.seebie.server.service.UserService;
import com.seebie.server.test.IntegrationTest;
import com.seebie.server.test.client.RestClientFactory;
import com.seebie.server.test.data.TestData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.of;
import static org.springframework.http.HttpMethod.GET;


/**
 * Unfortunately we weren't able to use WebMvcTest or vanilla SpringBootTest to access actuator endpoints.
 * (SpringBootTest approach was not retaining the test data in the database)
 * So we need a full live integration test to call them.
 * This could be revisited (maybe with @SpringBootTest, @AutoConfigureMockMvc, @EnableAutoConfiguration)
 */
public class ActuatorSecurityTest extends IntegrationTest {

    protected static Logger LOG = LoggerFactory.getLogger(ActuatorSecurityTest.class);

    private static RestClientFactory clientFactory;

    private static RestClient unAuthClient;
    private static RestClient adminClient;
    private static RestClient userClient;

    @BeforeAll
    public static void setup(@Autowired RestClient.Builder builder, @Autowired UserService userService) {

        // no /api prefix for actuator endpoints

        // we get the rest client builder as configured for the app, including mappers
        clientFactory = new RestClientFactory(builder, baseUribuilder.builder().build());

        adminClient = clientFactory.login("admin", "admin");

        var userRegistration = TestData.createRandomUserRegistration();
        userService.saveNewUser(userRegistration);
        userClient = clientFactory.login(userRegistration.username(), userRegistration.plainTextPassword());

        unAuthClient = clientFactory.noLogin();
    }

    private static final MultiValueMap<String,String> NO_PARAM = new LinkedMultiValueMap<>();
    private static final MultiValueMap<String, String> USER_PARAM = new LinkedMultiValueMap<>(Map.of("username", List.of("admin")));
    private static List<Arguments> provideUnauthenticatedTestParameters() {

        return List.of(
                of(GET, "/actuator", NO_PARAM, 401),
                of(GET, "/actuator/flyway", NO_PARAM, 401),
                of(GET, "/actuator/health", NO_PARAM, 401),
                of(GET, "/actuator/info", NO_PARAM, 401),
                of(GET, "/actuator/sessions", USER_PARAM, 401)
            );
    }

    private static List<Arguments> provideAdminTestParameters() {
        return List.of(
                of(GET, "/actuator", NO_PARAM, 200),
                of(GET, "/actuator/flyway", NO_PARAM, 200),
                of(GET, "/actuator/health", NO_PARAM, 200),
                of(GET, "/actuator/info", NO_PARAM, 200),
                of(GET, "/actuator/sessions", USER_PARAM, 200)
        );
    }

    private static List<Arguments> provideUserTestParameters() {
        return List.of(
                of(GET, "/actuator", NO_PARAM, 403),
                of(GET, "/actuator/flyway", NO_PARAM, 403),
                of(GET, "/actuator/health", NO_PARAM, 403),
                of(GET, "/actuator/info", NO_PARAM, 403),
                of(GET, "/actuator/sessions", USER_PARAM, 403)
        );
    }

    @ParameterizedTest
    @MethodSource("provideUnauthenticatedTestParameters")
    @DisplayName("Unauthenticated Access")
    void testUnauthenticatedSecurity(HttpMethod method, String urlPath, MultiValueMap<String,String> urlParams, int expectedStatus) {
        testSecurity(unAuthClient, method, urlPath, urlParams, expectedStatus);
    }

    @ParameterizedTest
    @MethodSource("provideAdminTestParameters")
    @DisplayName("Admin Access")
    void testAdminSecurity(HttpMethod method, String urlPath, MultiValueMap<String,String> urlParams, int expectedStatus) {
        testSecurity(adminClient, method, urlPath, urlParams, expectedStatus);
    }

    @ParameterizedTest
    @MethodSource("provideUserTestParameters")
    @DisplayName("User Access")
    void testUserSecurity(HttpMethod method, String urlPath, MultiValueMap<String,String> urlParams, int expectedStatus) {
        testSecurity(userClient, method, urlPath, urlParams, expectedStatus);
    }

    private void testSecurity(RestClient client, HttpMethod method, String urlPath, MultiValueMap<String,String> urlParams, int expectedStatus) {

        var uri = baseUribuilder.builder().path(urlPath).queryParams(urlParams).build();
        var req = client.mutate().build().method(method).uri(uri);
        LOG.info("Testing {} {}", method, uri);

        assertEquals(expectedStatus, req.retrieve().toBodilessEntity().getStatusCode().value());
    }
}
