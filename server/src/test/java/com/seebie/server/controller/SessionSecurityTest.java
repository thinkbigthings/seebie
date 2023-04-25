package com.seebie.server.controller;

import com.seebie.server.service.UserService;
import com.seebie.server.test.IntegrationTest;
import com.seebie.server.test.client.ApiClientStateful;
import com.seebie.server.test.data.TestData;
import org.junit.jupiter.api.BeforeAll;
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

import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


public class SessionSecurityTest extends IntegrationTest {

    private static final String SESSION_COOKIE = "SESSION";
    private static final String REMEMBER_ME_COOKIE = "remember-me";

    private static String testUserName;
    private static String testUserPassword;

    private static HttpRequest loginSession;
    private static HttpRequest loginRememberMe;
    private static HttpRequest userInfoRequest;

    private static Duration sessionTimeout;
    private static Duration rememberMeTimeout;

    @BeforeAll
    public static void setup(@LocalServerPort int randomServerPort,
                             @Autowired UserService userService,
                             @Autowired Environment env)
    {
        var userRegistration = TestData.createRandomUserRegistration();
        userService.saveNewUser(userRegistration);
        testUserName = userRegistration.username();
        testUserPassword = userRegistration.plainTextPassword();

        var baseUrl = "https://localhost:" + randomServerPort;
        var loginUri = URI.create(baseUrl + "/login?remember-me=false");
        var rememberMeUri = URI.create(baseUrl + "/login?remember-me=true");
        var securedUserUri = URI.create(baseUrl + "/user/" + userRegistration.username());

        loginSession = HttpRequest.newBuilder().GET().uri(loginUri).build();
        loginRememberMe = HttpRequest.newBuilder().GET().uri(rememberMeUri).build();
        userInfoRequest = HttpRequest.newBuilder().GET().uri(securedUserUri).build();

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
    public void testUnauthenticatedLoginCreatesNoSession() throws Exception {

        // We don't return session tokens for unauthenticated calls, it is unnecessary.
        // Also we don't want people to farm it for statistics on the cryptography of session tokens.

        // Attempt to access secured endpoint while unauthenticated
        var withoutAuth = ApiClientStateful.unAuthClient();
        var response = withoutAuth.send(loginSession, ofString());

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
    public void testSessionCookieExpiration() throws Exception {

        // Login without remember-me, and access secured endpoint
        var basicAuth = ApiClientStateful.basicAuthClient(testUserName, testUserPassword);
        var response = basicAuth.send(loginSession, ofString());
        assertResponse(response, 200, true, false);

        // Session cookie is set in first response, no cookie in second response
        var sessionAuth = ApiClientStateful.removeBasicAuth(basicAuth);
        response = sessionAuth.send(userInfoRequest, ofString());
        assertResponse(response, 200, false, false);

        // Wait for session timeout
        Thread.sleep(sessionTimeout.toMillis());

        // Then attempt to access secured endpoint
        // Result is a 401, Session is invalid, no session cookie is returned
        response = sessionAuth.send(userInfoRequest, ofString());
        assertResponse(response, 401, false, false);
    }

    @Test
    public void testRememberMeSessionExpiration() throws Exception {

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
        Thread.sleep(sessionTimeout.toMillis());

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
    public void testRememberMeCookieExpiration() throws Exception {

        // Login with remember-me and access secured endpoint
        // Result is a 200, Session cookie is set and remember-me cookie is set
        var basicAuth = ApiClientStateful.basicAuthClient(testUserName, testUserPassword);
        var response = basicAuth.send(loginRememberMe, ofString());
        assertResponse(response, 200, true, true);

        // subsequent requests should NOT have session cookie set, cookie is sent in subsequent requests
        var sessionAuth = ApiClientStateful.removeBasicAuth(basicAuth);
        response = sessionAuth.send(userInfoRequest, ofString());
        assertResponse(response, 200, false, false);

        // Wait for remember-me timeout, then attempt to access secured endpoint again
        // Result is a 401, Session is invalid, no session cookie is returned, remember-me cookie is cleared

        Thread.sleep(rememberMeTimeout.toMillis());

        response = sessionAuth.send(userInfoRequest, ofString());
        assertResponse(response, 401, false, true);
        assertEquals("", getCookie(response, REMEMBER_ME_COOKIE).getValue());

        // once remember me cookie is cleared, any subsequent response will not try to set either cookie
        response = sessionAuth.send(userInfoRequest, ofString());
        assertResponse(response, 401, false, false);

        // TODO each test should have its own test user so these could be run in parallel

        // TODO monitor remote build, it is failing

        // TODO this is setup differently in EndToEndIntegrationTest, should be consistent

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
