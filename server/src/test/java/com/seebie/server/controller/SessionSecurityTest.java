package com.seebie.server.controller;

import com.seebie.server.service.UserService;
import com.seebie.server.test.IntegrationTest;
import com.seebie.server.test.client.ApiClientStateful;
import com.seebie.server.test.data.TestData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.env.Environment;

import java.net.HttpCookie;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.seebie.server.security.WebSecurityConfig.REMEMBER_ME_COOKIE;
import static com.seebie.server.security.WebSecurityConfig.SESSION_COOKIE;
import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


public class SessionSecurityTest extends IntegrationTest {

    protected static String baseUrl;

    protected static URI loginUri;
    protected static URI rememberMeUri;

    protected static HttpRequest loginRequest;
    protected static HttpRequest loginRememberMe;

    private static Duration sessionTimeout;
    private static Duration rememberMeTimeout;

    private String testUserName;
    private String testUserPassword;
    private HttpRequest userInfoRequest;

    @BeforeEach
    public void setupTestUser(@Autowired UserService userService) {

        // each test should have its own test user so these could be run in parallel

        var userRegistration = TestData.createRandomUserRegistration();
        userService.saveNewUser(userRegistration);

        testUserName = userRegistration.username();
        testUserPassword = userRegistration.plainTextPassword();

        userInfoRequest = HttpRequest.newBuilder()
                .uri(URI.create(STR."\{baseUrl}/user/\{userRegistration.username()}"))
                .GET()
                .build();
    }

    @BeforeAll
    public static void setup(@Autowired Environment env, @LocalServerPort int randomServerPort) {

        baseUrl = STR."https://localhost:\{randomServerPort}/api";

        loginUri = URI.create(baseUrl + "/login?remember-me=false");
        rememberMeUri = URI.create(baseUrl + "/login?remember-me=true");

        loginRequest = HttpRequest.newBuilder().GET().uri(loginUri).build();
        loginRememberMe = HttpRequest.newBuilder().GET().uri(rememberMeUri).build();

        // See timeout values set in IntegrationTest
        sessionTimeout = env.getProperty("spring.session.timeout", Duration.class);
        rememberMeTimeout = env.getProperty("app.security.rememberMe.tokenValidity", Duration.class);
    }

    @Test
    public void testUnauthenticatedCallFails() throws Exception {

        // We don't return session tokens for unauthenticated calls, it is unnecessary.
        // Also, we don't want people to farm it for statistics on the cryptography of session tokens.

        // Attempt to access secured endpoint while unauthenticated
        var withoutAuth = ApiClientStateful.unAuthClient();
        var response = withoutAuth.send(userInfoRequest, ofString());

        assertResponse(response, 401, false, false);
    }

    @Test
    public void testUnauthenticatedLoginCreatesNoSessions() throws Exception {

        // We don't return session tokens for unauthenticated calls, it is unnecessary.
        // Also we don't want people to farm it for statistics on the cryptography of session tokens.

        // Attempt to access secured endpoint while unauthenticated
        var withoutAuth = ApiClientStateful.unAuthClient();
        var response = withoutAuth.send(loginRequest, ofString());

        assertResponse(response, 401, false, false);
    }

    @Test
    public void testUnauthenticatedLoginCreatesNoRememberMe() throws Exception {

        // We don't return session tokens for unauthenticated calls, it is unnecessary.
        // Also we don't want people to farm it for statistics on the cryptography of session tokens.

        // Attempt to access secured endpoint while unauthenticated
        var withoutAuth = ApiClientStateful.unAuthClient();
        var response = withoutAuth.send(loginRememberMe, ofString());

        assertResponse(response, 401, false, false);
    }

    @Test
    public void testSessionCookieTimeoutWithoutRememberMe() throws Exception {

        // Login without remember-me, and access secured endpoint
        var basicAuth = ApiClientStateful.basicAuthClient(testUserName, testUserPassword);
        var response = basicAuth.send(loginRequest, ofString());
        assertResponse(response, 200, true, false);

        // Session cookie is set in first response, no cookie in second response
        var sessionAuth = ApiClientStateful.removeBasicAuth(basicAuth);
        response = sessionAuth.send(userInfoRequest, ofString());
        assertResponse(response, 200, false, false);

        waitForExpiration(sessionTimeout);

        // Then attempt to access secured endpoint
        // Result is a 401, Session is invalid, no session cookie is returned
        response = sessionAuth.send(userInfoRequest, ofString());
        assertResponse(response, 401, false, false);
    }

    @Test
    public void testSessionCookieTimeoutWithRememberMe() throws Exception {

        // Login with remember-me and access secured endpoint
        // Result is a 200, Session cookie is set and remember-me cookie is set
        var basicAuth = ApiClientStateful.basicAuthClient(testUserName, testUserPassword);
        var response = basicAuth.send(loginRememberMe, ofString());
        var originalSessionCookie = getCookie(response, SESSION_COOKIE);
        var originalRememberMeCookie = getCookie(response, REMEMBER_ME_COOKIE);

        assertResponse(response, 200, true, true);

        // subsequent requests should NOT have session cookie set, cookie is sent in subsequent requests
        var sessionAuth = ApiClientStateful.removeBasicAuth(basicAuth);
        response = sessionAuth.send(userInfoRequest, ofString());

        assertResponse(response, 200, false, false);

        // Wait for session timeout but don't let remember-me timeout
        waitForExpiration(sessionTimeout);

        // Then attempt to access secured endpoint again
        response = sessionAuth.send(userInfoRequest, ofString());
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
        var basicAuth = ApiClientStateful.basicAuthClient(testUserName, testUserPassword);
        var response = basicAuth.send(loginRememberMe, ofString());
        assertResponse(response, 200, true, true);

        // subsequent requests should NOT have session cookie set, cookie is sent from client in subsequent requests
        var sessionAuth = ApiClientStateful.removeBasicAuth(basicAuth);
        response = sessionAuth.send(userInfoRequest, ofString());
        assertResponse(response, 200, false, false);

        // Wait for remember-me timeout, then attempt to access secured endpoint again
        // Result is a 401, Session is invalid, no session cookie is returned, remember-me cookie is cleared

        waitForExpiration(rememberMeTimeout);

        response = sessionAuth.send(userInfoRequest, ofString());
        assertResponse(response, 401, false, true);
        assertEquals("", getCookie(response, REMEMBER_ME_COOKIE).getValue());

        // once remember me cookie is cleared, any subsequent response will not try to set either cookie
        response = sessionAuth.send(userInfoRequest, ofString());
        assertResponse(response, 401, false, false);
    }

    private void waitForExpiration(Duration timeout) throws Exception {
        Thread.sleep(timeout.toMillis());
    }

    public void assertResponse(HttpResponse response, int expectedStatusCode, boolean setsSessionCookie, boolean setsRememberMeCookie) {
        assertEquals(expectedStatusCode, response.statusCode());
        assertEquals(setsSessionCookie, findCookie(response, SESSION_COOKIE).isPresent());
        assertEquals(setsRememberMeCookie, findCookie(response, REMEMBER_ME_COOKIE).isPresent());
    }

    public HttpCookie getCookie(HttpResponse response, String cookieName) {
        return findCookie(response, cookieName).get();
    }

    public Optional<HttpCookie> findCookie(HttpResponse response, String cookieName) {
        return response.headers().map().getOrDefault("SET-COOKIE", new ArrayList<>())
                .stream()
                .map(HttpCookie::parse)
                .flatMap(List::stream)
                .filter(c -> c.getName().equals(cookieName))
                .findFirst();
    }

}
