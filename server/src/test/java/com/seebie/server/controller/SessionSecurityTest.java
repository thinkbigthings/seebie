package com.seebie.server.controller;

import com.seebie.server.service.UserService;
import com.seebie.server.test.IntegrationTest;
import com.seebie.server.test.client.RestClientFactory;
import com.seebie.server.test.data.TestData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.net.HttpCookie;
import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.seebie.server.security.WebSecurityConfig.REMEMBER_ME_COOKIE;
import static com.seebie.server.security.WebSecurityConfig.SESSION_COOKIE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


public class SessionSecurityTest extends IntegrationTest {

    protected static URI loginWithoutRememberMeUri;
    protected static URI loginWithRememberMeUri;

    private static Duration sessionTimeout;
    private static Duration rememberMeTimeout;

    private static RestClientFactory clientFactory;
    private static DefaultUriBuilderFactory uriBuilderFactory;

    private String testUserName;
    private String testUserPassword;
    private URI testUserInfoUri;

    @BeforeEach
    public void setupTestUser(@Autowired UserService userService) {

        // each test has its own test user so these could be run in parallel

        var userRegistration = TestData.createRandomUserRegistration();
        userService.saveNewUser(userRegistration);

        testUserName = userRegistration.username();
        testUserPassword = userRegistration.plainTextPassword();

        testUserInfoUri = uriBuilderFactory.builder().path("/user/").path(userRegistration.username()).build();
    }

    @BeforeAll
    public static void setup(@Autowired Environment env,
                             @Autowired RestClient.Builder builder,
                             @LocalServerPort int randomServerPort)
    {
        uriBuilderFactory = new DefaultUriBuilderFactory(STR."https://localhost:\{randomServerPort}/api");

        // we get the rest client builder as configured for the app, including mappers
        clientFactory = new RestClientFactory(builder, randomServerPort);

        var loginBuilder = uriBuilderFactory.builder().path("/login");
        loginWithoutRememberMeUri = loginBuilder.replaceQueryParam("remember-me", "false").build();
        loginWithRememberMeUri =  loginBuilder.replaceQueryParam("remember-me", "true").build();

        // See timeout values set in IntegrationTest, they configure the server so that we can time out here
        sessionTimeout = env.getProperty("spring.session.timeout", Duration.class);
        rememberMeTimeout = env.getProperty("app.security.rememberMe.tokenValidity", Duration.class);
    }

    @Test
    public void testUnauthenticatedCallFails() {

        // We don't return session tokens for unauthenticated calls, it is unnecessary.
        // Also, we don't want people to farm it for statistics on the cryptography of session tokens.

        // Attempt to access secured endpoint while unauthenticated
        var response = clientFactory.noLogin().get().uri(testUserInfoUri).retrieve().toEntity(String.class);

        assertResponse(response, 401, false, false);
    }

    @Test
    public void testUnauthenticatedLoginCreatesNoSessions() {

        // We don't return session tokens for unauthenticated calls, it is unnecessary.
        // Also, we don't want people to farm it for statistics on the cryptography of session tokens.

        // Attempt to access secured endpoint while unauthenticated
        var response = clientFactory.noLogin().get().uri(loginWithoutRememberMeUri).retrieve().toEntity(String.class);

        assertResponse(response, 401, false, false);
    }

    @Test
    public void testUnauthenticatedLoginCreatesNoRememberMe() {

        // We don't return session tokens for unauthenticated calls, it is unnecessary.
        // Also, we don't want people to farm it for statistics on the cryptography of session tokens.

        // Attempt to access secured endpoint while unauthenticated
        var response = clientFactory.noLogin().get().uri(loginWithRememberMeUri).retrieve().toEntity(String.class);

        assertResponse(response, 401, false, false);
    }

    @Test
    public void testSessionCookieTimeoutWithoutRememberMe() {

        // Login without remember-me, and access secured endpoint
        var basicAuth = clientFactory.basicAuth(testUserName, testUserPassword);
        var response = clientFactory.fromHttpClient(basicAuth).get().uri(loginWithoutRememberMeUri).retrieve().toEntity(String.class);
        assertResponse(response, 200, true, false);

        // Session cookie is set in first response, no cookie in second response
        var sessionAuth = clientFactory.removeBasicAuth(basicAuth);
        response = clientFactory.fromHttpClient(sessionAuth).get().uri(testUserInfoUri).retrieve().toEntity(String.class);
        assertResponse(response, 200, false, false);

        waitForExpiration(sessionTimeout);

        // Then attempt to access secured endpoint
        // Result is a 401, Session is invalid, no session cookie is returned
        response = clientFactory.fromHttpClient(sessionAuth).get().uri(testUserInfoUri).retrieve().toEntity(String.class);
        assertResponse(response, 401, false, false);
    }

    @Test
    public void testSessionCookieTimeoutWithRememberMe() throws Exception {

        // Login with remember-me and access secured endpoint
        // Result is a 200, Session cookie is set and remember-me cookie is set
        var basicAuth = clientFactory.basicAuth(testUserName, testUserPassword);
        var response = clientFactory.fromHttpClient(basicAuth).get().uri(loginWithRememberMeUri).retrieve().toEntity(String.class);
        var originalSessionCookie = getCookie(response, SESSION_COOKIE);
        var originalRememberMeCookie = getCookie(response, REMEMBER_ME_COOKIE);
        assertResponse(response, 200, true, true);

        // subsequent requests should NOT have session cookie set, cookie is sent in subsequent requests
        var sessionAuth = clientFactory.removeBasicAuth(basicAuth);
        response = clientFactory.fromHttpClient(sessionAuth).get().uri(testUserInfoUri).retrieve().toEntity(String.class);
        assertResponse(response, 200, false, false);

        // Wait for session timeout but don't let remember-me timeout
        waitForExpiration(sessionTimeout);

        // Then attempt to access secured endpoint again
        response = clientFactory.fromHttpClient(sessionAuth).get().uri(testUserInfoUri).retrieve().toEntity(String.class);
        assertResponse(response, 200, true, true);
        var newSessionCookie = getCookie(response, SESSION_COOKIE);
        var newRememberMeCookie = getCookie(response, REMEMBER_ME_COOKIE);

        // New session cookie is issued, new remember-me cookie is issued
        assertNotEquals(originalSessionCookie.getValue(), newSessionCookie.getValue());
        assertNotEquals(originalRememberMeCookie.getValue(), newRememberMeCookie.getValue());
    }

    @Test
    public void testRememberMeCookieTimeout() throws Exception {

        // Login with remember-me and access secured endpoint
        // Result is a 200, Session cookie is set and remember-me cookie is set
        var basicAuth = clientFactory.basicAuth(testUserName, testUserPassword);
        var response = clientFactory.fromHttpClient(basicAuth).get().uri(loginWithRememberMeUri).retrieve().toEntity(String.class);
        assertResponse(response, 200, true, true);

        // subsequent requests should NOT have session cookie set, cookie is sent in subsequent requests
        var sessionAuth = clientFactory.removeBasicAuth(basicAuth);
        response = clientFactory.fromHttpClient(sessionAuth).get().uri(testUserInfoUri).retrieve().toEntity(String.class);
        assertResponse(response, 200, false, false);

        // Wait for remember-me timeout
        waitForExpiration(rememberMeTimeout);

        // then attempt to access secured endpoint again
        // Result is a 401, Session is invalid, no session cookie is returned, remember-me cookie is cleared
        response = clientFactory.fromHttpClient(sessionAuth).get().uri(testUserInfoUri).retrieve().toEntity(String.class);
        assertResponse(response, 401, false, true);
        assertEquals("", getCookie(response, REMEMBER_ME_COOKIE).getValue());

        // once remember me cookie is cleared, any subsequent response will not try to set either cookie
        response = clientFactory.fromHttpClient(sessionAuth).get().uri(testUserInfoUri).retrieve().toEntity(String.class);
        assertResponse(response, 401, false, false);
    }

    private void waitForExpiration(Duration timeout) {
        try {
            Thread.sleep(timeout.toMillis());
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public HttpCookie getCookie(ResponseEntity<String> response, String cookieName) {
        return findCookie(response, cookieName).get();
    }

    public void assertResponse(ResponseEntity<String> response, int expectedStatusCode, boolean setsSessionCookie, boolean setsRememberMeCookie) {
        assertEquals(expectedStatusCode, response.getStatusCode().value());
        assertEquals(setsSessionCookie, findCookie(response, SESSION_COOKIE).isPresent());
        assertEquals(setsRememberMeCookie, findCookie(response, REMEMBER_ME_COOKIE).isPresent());
    }

    public Optional<HttpCookie> findCookie(ResponseEntity<String> response, String cookieName) {
        return response.getHeaders().getOrDefault("SET-COOKIE", new ArrayList<>())
                .stream()
                .map(HttpCookie::parse)
                .flatMap(List::stream)
                .filter(c -> c.getName().equals(cookieName))
                .findFirst();
    }
}
