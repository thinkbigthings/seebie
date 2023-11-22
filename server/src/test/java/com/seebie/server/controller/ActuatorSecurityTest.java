package com.seebie.server.controller;

import com.seebie.server.service.UserService;
import com.seebie.server.test.IntegrationTest;
import com.seebie.server.test.client.ApiClientStateful;
import com.seebie.server.test.client.RestClientFactory;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilderFactory;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private static RestClientFactory clientFactory;
    private static ApiClientStateful unAuthClient;

    private static RestClient adminClient;
    private static RestClient userClient;

    private static Function<AppRequest, HttpRequest> toRequest;

    private static ArgumentBuilder restTest;
    private static TestData.ArgumentBuilder test;

    private static DefaultUriBuilderFactory uriBuilderFactory;

    @BeforeAll
    public static void setup(@Autowired RestClient.Builder builder, @LocalServerPort int randomServerPort,
                             @Autowired UserService userService,
                             @Autowired MappingJackson2HttpMessageConverter converter)
    {


        // so we get the mapper as configured for the app
        toRequest = new HttpRequestMapper(testDataObj2Str(converter.getObjectMapper()));

        String baseUrl = STR."https://localhost:\{randomServerPort}";
        uriBuilderFactory = new DefaultUriBuilderFactory(baseUrl);

        // so we get the rest client builder as configured for the app, including mappers
        clientFactory = new RestClientFactory(builder, randomServerPort);

        adminClient = clientFactory.createLoggedInClient("admin", "admin");
        restTest = new ArgumentBuilder(uriBuilderFactory);

        test = new TestData.ArgumentBuilder(baseUrl);


        var userRegistration = TestData.createRandomUserRegistration();
        userService.saveNewUser(userRegistration);
        userClient = clientFactory.createLoggedInClient(userRegistration.username(), userRegistration.plainTextPassword());

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
				restTest.get("/actuator", 200),
                restTest.get("/actuator/flyway", 200),
                restTest.get("/actuator/health", 200),
                restTest.get("/actuator/info", 200),
                restTest.get("/actuator/sessions", Map.of("username", "admin"), 200)
        );
    }

    private static List<Arguments> provideUserTestParameters() {
        return List.of(
                restTest.get("/actuator", 403),
                restTest.get("/actuator/flyway", 403),
                restTest.get("/actuator/health", 403),
                restTest.get("/actuator/info", 403),
                restTest.get("/actuator/sessions", Map.of("username", "admin"), 403)
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
    void testAdminSecurity(HttpMethod method, URI uri, int expectedStatus) {
        var req = adminClient.mutate()
                .defaultStatusHandler(status -> status.is4xxClientError(), (request,resp) -> {})
                .build().method(method).uri(uri);
        assertEquals(expectedStatus, req.retrieve().toBodilessEntity().getStatusCode().value());
    }

    @ParameterizedTest
    @MethodSource("provideUserTestParameters")
    @DisplayName("User Access")
    void testUserSecurity(HttpMethod method, URI uri, int expectedStatus) {
        var req = userClient.mutate()
                .defaultStatusHandler(status -> status.is4xxClientError(), (request,resp) -> {})
                .build().method(method).uri(uri);
        assertEquals(expectedStatus, req.retrieve().toBodilessEntity().getStatusCode().value());
    }

    public static class ArgumentBuilder {

        private UriBuilderFactory uriBuilderFactory;

        public ArgumentBuilder(UriBuilderFactory uriBuilderFactory) {
            this.uriBuilderFactory = uriBuilderFactory;
        }

        public Arguments get(String urlPath, Map<String,String> requestParams, int expected) {

            var uriBuilder = uriBuilderFactory.builder().path(urlPath);
            requestParams.forEach((key, value) -> uriBuilder.queryParam(key, value));

            return Arguments.of(HttpMethod.GET, uriBuilder.build(), expected);
        }

        public Arguments get(String urlPath, int expected) {
            return get(urlPath, new HashMap<>(), expected);
        }

    }
}
