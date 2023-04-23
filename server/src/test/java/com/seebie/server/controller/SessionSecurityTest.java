package com.seebie.server.controller;

import com.seebie.server.service.UserService;
import com.seebie.server.test.IntegrationTest;
import com.seebie.server.test.client.ApiClientStateful;
import com.seebie.server.test.data.TestData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;


public class SessionSecurityTest extends IntegrationTest {

    private static String baseUrl;

    private static ApiClientStateful adminClient;
    private static ApiClientStateful userClient;
    private static ApiClientStateful unAuthClient;

    @BeforeAll
    public static void setup(@LocalServerPort int randomServerPort,
                             @Autowired UserService userService,
                             @Autowired MappingJackson2HttpMessageConverter converter)
    {
        baseUrl = "https://localhost:" + randomServerPort;

        adminClient = new ApiClientStateful(baseUrl, "admin", "admin");

        var userRegistration = TestData.createRandomUserRegistration();
        userService.saveNewUser(userRegistration);
        userClient = new ApiClientStateful(baseUrl, userRegistration.username(), userRegistration.plainTextPassword());

        unAuthClient = new ApiClientStateful();
    }


    /**
     * We don't return session tokens for unauthenticated calls.
     * For one, it's unnecessary. Also:
     * we don't want people to farm it for statistics on the cryptography of session tokens.
     *
     * @throws Exception
     */
    @Test
    void testNoSessionForUnauthenticatedCall() throws Exception {

        var unAuthLogin = HttpRequest.newBuilder()
                .GET()
                .uri(new URI(baseUrl + "/login"))
                .build();

        var response = unAuthClient.trySend(unAuthLogin);

        boolean hasSession = response.headers().map().entrySet().stream()
                .filter(entry -> entry.getKey().equalsIgnoreCase("SET-COOKIE"))
                .map(Map.Entry::getValue)
                .flatMap(List::stream)
                .filter(value -> value.contains("SESSION"))
                .findAny().isPresent();

        assertFalse(hasSession, "Unauthorized calls should not create sessions");
    }
}
