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

import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static org.junit.jupiter.api.Assertions.*;


public class SessionSecurityTest extends IntegrationTest {


    private static String testUserName;
    private static String testUserPassword;

    private static HttpRequest loginRequest;
    private static HttpRequest loginRememberMe;
    private static HttpRequest userInfoRequest;

    private static Duration sessionTimeout;
    private static Duration rememberMeTimeout;

    @BeforeAll
    public static void setup(@LocalServerPort int randomServerPort,
                             @Autowired UserService userService,
                             @Autowired Environment env)
    {

        // TODO this is built differently in EndToEndIntegrationTest

        var userRegistration = TestData.createRandomUserRegistration();
        userService.saveNewUser(userRegistration);
        testUserName = userRegistration.username();
        testUserPassword = userRegistration.plainTextPassword();

        var baseUrl = "https://localhost:" + randomServerPort;
        var loginUri = URI.create(baseUrl + "/login");
        var rememberMeUri = URI.create(baseUrl + "/login?remember-me=true");
        var securedUserUri = URI.create(baseUrl + "/user/" + userRegistration.username());

        loginRequest = HttpRequest.newBuilder().GET().uri(loginUri).build();
        loginRememberMe = HttpRequest.newBuilder().GET().uri(rememberMeUri).build();
        userInfoRequest = HttpRequest.newBuilder().GET().uri(securedUserUri).build();

        sessionTimeout = env.getProperty("spring.session.timeout", Duration.class);
        rememberMeTimeout = env.getProperty("app.security.rememberMe.tokenValidity", Duration.class);

    }

    @Test
    public void testSessionAndRememberMeWorkflow() throws Exception {

        /*
         * We don't return session tokens for unauthenticated calls.
         * For one, it's unnecessary. Also:
         * we don't want people to farm it for statistics on the cryptography of session tokens.
         */

        // Attempt to access secured endpoint while unauthenticated
        // Result is a 401, no cookies are set
        // curl -kv  "https://localhost:9000/user/admin"
        var unauthenticated = ApiClientStateful.unAuthClient();
        var response = unauthenticated.send(userInfoRequest, ofString());
        var setsSessionCookie = hasCookie(response, "SESSION");
        var setsRememberMeCookie = hasCookie(response, "remember-me");

        assertEquals(401, response.statusCode(), "Unauthorized calls should return 401");
        assertFalse(setsSessionCookie, "Unauthorized calls should not create sessions");
        assertFalse(setsRememberMeCookie, "Unauthorized calls should not create remember-me tokens");

        // TODO test that remember-me token isn't created if the flag is set but the call is unauthenticated


        // Login without remember-me and access secured endpoint
        // curl -kv -b cookies.txt -c cookies.txt --user admin:admin "https://localhost:9000/login?remember-me=false"
        // curl -kv -b cookies.txt -c cookies.txt "https://localhost:9000/user/admin"
        // Result is a 200, Session cookie is set in first response, no cookie in second response

        var authenticated = ApiClientStateful.basicAuthClient(testUserName, testUserPassword);
        response = authenticated.send(loginRequest, ofString());
        setsSessionCookie = hasCookie(response, "SESSION");
        setsRememberMeCookie = hasCookie(response, "remember-me");

        assertEquals(200, response.statusCode());
        assertTrue(setsSessionCookie);
        assertFalse(setsRememberMeCookie);

        authenticated = ApiClientStateful.removeBasicAuth(authenticated);
        response = authenticated.send(userInfoRequest, ofString());
        setsSessionCookie = hasCookie(response, "SESSION");
        setsRememberMeCookie = hasCookie(response, "remember-me");

        assertEquals(200, response.statusCode());
        assertFalse(setsSessionCookie);
        assertFalse(setsRememberMeCookie);

        // Wait for session timeout, then attempt to access secured endpoint
        // See timeout values in IntegrationTest, they must correspond to the values here
        // curl -kv -b cookies.txt -c cookies.txt "https://localhost:9000/user/admin"
        // Result is a 401, Session is invalid, no session cookie is returned


        Thread.sleep(sessionTimeout.toMillis());

        response = authenticated.send(userInfoRequest, ofString());
        setsSessionCookie = hasCookie(response, "SESSION");
        setsRememberMeCookie = hasCookie(response, "remember-me");

        assertEquals(401, response.statusCode());
        assertFalse(setsSessionCookie);
        assertFalse(setsRememberMeCookie);

        ////////////////////////////////////////////////////////

//        login with remember-me and access secured endpoint
//        curl -kv -b cookies.txt -c cookies.txt --user admin:admin "https://localhost:9000/login?remember-me=true"
//        curl -kv -b cookies.txt -c cookies.txt "https://localhost:9000/user/admin"
//        result is a 200, Session cookie is set and remember-me cookie is set

        authenticated = ApiClientStateful.basicAuthClient(testUserName, testUserPassword);
        response = authenticated.send(loginRememberMe, ofString());
        setsSessionCookie = hasCookie(response, "SESSION");
        setsRememberMeCookie = hasCookie(response, "remember-me");

        assertEquals(200, response.statusCode());
        assertTrue(setsSessionCookie);
        assertTrue(setsRememberMeCookie);

        authenticated = ApiClientStateful.removeBasicAuth(authenticated);
        response = authenticated.send(userInfoRequest, ofString());
        setsSessionCookie = hasCookie(response, "SESSION");
        setsRememberMeCookie = hasCookie(response, "remember-me");

        assertEquals(200, response.statusCode());
        assertFalse(setsSessionCookie);
        assertFalse(setsRememberMeCookie);

//        Wait for session timeout, then attempt to access secured endpoint again
//        curl -kv -b cookies.txt -c cookies.txt "https://localhost:9000/user/admin"
//        Result is a 200, Session cookie is set to a new session id

        Thread.sleep(sessionTimeout.toMillis());

        response = authenticated.send(userInfoRequest, ofString());
        setsSessionCookie = hasCookie(response, "SESSION");
        setsRememberMeCookie = hasCookie(response, "remember-me");

        // TODO I think remember-me is reset with new expiration (same token) when session is reset
        // < Set-Cookie: remember-me=bE80NmxOQTdKczl4SzlNRWNPVFdxUSUzRCUzRDpYbklWT05wc3hDREQwbVMzMVhUVzN3JTNEJTNE; Max-Age=60; Expires=Sun, 23-Apr-2023 11:12:37 GMT; Path=/; Secure; HttpOnly
        // can detect the expiration date and see if that changes?
        // try saving the cookie values to detect where they are changing?
        assertEquals(200, response.statusCode());
        assertTrue(setsSessionCookie);
//        assertFalse(setsRememberMeCookie);

        // TODO test that the session id is different from the previous one ?

//        Wait for remember-me timeout, then attempt to access secured endpoint again
//        curl -kv -b cookies.txt -c cookies.txt "https://localhost:9000/user/admin"
//        Result is a 401, Session is invalid, no session cookie or remember-me cookie is returned

//        Thread.sleep(rememberMeTimeout.toMillis());
//
//        response = authenticated.send(userInfoRequest, ofString());
//        setsSessionCookie = hasCookie(response, "SESSION");
//        setsRememberMeCookie = hasCookie(response, "remember-me");
//
//        assertEquals(401, response.statusCode());
//        assertFalse(setsSessionCookie);
//        assertFalse(setsRememberMeCookie);

        // TODO time the whole test, see how much we can reduce the timeouts and have the other tests still work.
        // maybe can split into smaller scenarios so we can get a big test time boost when we parallelize the tests?
    }

    public boolean hasCookie(HttpResponse response, String cookieName) {
        return response.headers().map().getOrDefault("SET-COOKIE", new ArrayList<>())
                .stream()
                .map(HttpCookie::parse)
                .flatMap(List::stream)
                .anyMatch(c -> c.getName().equals(cookieName));
    }

}
