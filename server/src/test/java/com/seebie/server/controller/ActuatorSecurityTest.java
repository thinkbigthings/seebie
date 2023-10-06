package com.seebie.server.controller;

import com.seebie.server.service.UserService;
import com.seebie.server.test.IntegrationTest;
import com.seebie.server.test.client.ApiClientStateful;
import com.seebie.server.test.data.AppRequest;
import com.seebie.server.test.data.HttpRequestMapper;
import com.seebie.server.test.data.TestData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.net.http.HttpRequest;
import java.util.List;
import java.util.function.Function;

import static com.seebie.server.controller.ControllerValidationTest.testDataObj2Str;
import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Unfortunately we weren't able to use WebMvcTest or vanilla SpringBootTest to access actuator endpoints.
 * (SpringBootTest approach was not retaining the test data in the database)
 * So we need a full live integration test to call them.
 * This could be revisited (maybe with @SpringBootTest, @AutoConfigureMockMvc, @EnableAutoConfiguration)
 */
public class ActuatorSecurityTest extends IntegrationTest {

    private static String baseUrl;

    private static ApiClientStateful adminClient;
    private static ApiClientStateful userClient;
    private static ApiClientStateful unAuthClient;

    private static Function<AppRequest, HttpRequest> toRequest;

    private static TestData.ArgumentBuilder test;

    @BeforeAll
    public static void setup(@LocalServerPort int randomServerPort,
                             @Autowired UserService userService,
                             @Autowired MappingJackson2HttpMessageConverter converter)
    {
        // so we get the mapper as configured for the app
        toRequest = new HttpRequestMapper(testDataObj2Str(converter));

        baseUrl = "https://localhost:" + randomServerPort;

        test = new TestData.ArgumentBuilder(baseUrl);

        adminClient = new ApiClientStateful(baseUrl, "admin", "admin");

        var userRegistration = TestData.createRandomUserRegistration();
        userService.saveNewUser(userRegistration);
        userClient = new ApiClientStateful(baseUrl, userRegistration.username(), userRegistration.plainTextPassword());

        unAuthClient = new ApiClientStateful();
    }

    private static List<Arguments> provideUnauthenticatedTestParameters() {

        return List.of(
				test.get("/actuator", 401),
				test.get("/actuator/flyway", 401),
				test.get("/actuator/health", 401),
				test.get("/actuator/info", 401),
				test.get("/actuator/sessions", new String[] {"username", "admin"}, 401)
            );
    }

    private static List<Arguments> provideAdminTestParameters() {
        return List.of(
				test.get("/actuator", 200),
				test.get("/actuator/flyway", 200),
				test.get("/actuator/health", 200),
				test.get("/actuator/info", 200),
				test.get("/actuator/sessions", new String[]{"username", "admin"}, 200)
        );
    }

    private static List<Arguments> provideUserTestParameters() {
        return List.of(
                test.get("/actuator", 403),
				test.get("/actuator/flyway", 403),
				test.get("/actuator/health", 403),
				test.get("/actuator/info", 403),
				test.get("/actuator/sessions",new String[]{"username", "admin"}, 403)
        );
    }

    @ParameterizedTest
    @MethodSource("provideUnauthenticatedTestParameters")
    @DisplayName("Unauthenticated Access")
    void testUnauthenticatedSecurity(AppRequest testData, int expectedStatus) throws Exception {
        assertEquals(expectedStatus, unAuthClient.trySend(toRequest.apply(testData)).statusCode());
    }

    @ParameterizedTest
    @MethodSource("provideAdminTestParameters")
    @DisplayName("Admin Access")
    void testAdminSecurity(AppRequest testData, int expectedStatus) throws Exception {
        assertEquals(expectedStatus, adminClient.trySend(toRequest.apply(testData)).statusCode());
    }

    @ParameterizedTest
    @MethodSource("provideUserTestParameters")
    @DisplayName("User Access")
    void testUserSecurity(AppRequest testData, int expectedStatus) throws Exception {
        assertEquals(expectedStatus, userClient.trySend(toRequest.apply(testData)).statusCode());
    }

}
