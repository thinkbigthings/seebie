package com.seebie.server.controller;

import ch.qos.logback.classic.LoggerContext;
import com.seebie.server.MemoryAppender;
import com.seebie.server.repository.PersistentLoginRepository;
import com.seebie.server.service.PersistentLoginSchedulingService;
import com.seebie.server.service.UserService;
import com.seebie.server.test.IntegrationTest;
import com.seebie.server.test.client.RestClientFactory;
import com.seebie.server.test.data.TestData;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import java.net.HttpCookie;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static com.seebie.server.Functional.toExactlyOne;
import static com.seebie.server.Functional.toOne;
import static com.seebie.server.security.WebSecurityConfig.REMEMBER_ME_COOKIE;
import static com.seebie.server.security.WebSecurityConfig.SESSION_COOKIE;
import static org.junit.jupiter.api.Assertions.*;

import static com.seebie.server.controller.SessionSecurityTest.CookieResponse.*;

public class SessionSecurityTest extends IntegrationTest {

    protected static URI loginRememberMeFalseUri;
    protected static URI loginRememberMeTrueUri;
    protected static URI loginUri;
    protected static URI logoutUri;

    private static Duration sessionTimeout;
    private static Duration rememberMeTimeout;

    private RestClientFactory clientFactory;
    private static PersistentLoginRepository persistentLoginRepository;
    private static PersistentLoginSchedulingService schedulingService;

    private String testUserName;
    private String testUserPassword;
    private URI testUserInfoUri;

    // logging of important security events is set up through configuration and not logged directly in the code,
    // so we have integration tests for logging to ensure that the configuration was correct.
    private static final MemoryAppender memoryAppender = new MemoryAppender();

    public enum CookieResponse {
        SET, // cookie name and value are present
        NOT_SET, // cookie name is not even present
        CLEARED // cookie name is present but value is empty
    }

    @BeforeAll
    public static void setupRepos(@Autowired PersistentLoginRepository persistentLoginRepo, @Autowired PersistentLoginSchedulingService reaper) {
        persistentLoginRepository = persistentLoginRepo;
        schedulingService = reaper;
    }

    @BeforeAll
    public static void setupLogging() {
        var loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        var rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
        memoryAppender.setContext(loggerContext);
        rootLogger.addAppender(memoryAppender);
        memoryAppender.start();
    }

    @AfterAll
    public static void teardownLogging() {
        var loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        var rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.detachAppender(memoryAppender);
        memoryAppender.stop();
    }

    @BeforeEach
    public void setupTestUser(@Autowired UserService userService) {

        // each test has its own test user so each test can be run in parallel

        var userRegistration = TestData.createRandomUserRegistration();
        userService.saveNewUser(userRegistration);

        testUserName = userRegistration.username();
        testUserPassword = userRegistration.plainTextPassword();

        var apiUriBuilder = baseUribuilder.builder().path("/api");
        testUserInfoUri = apiUriBuilder.path("/user/").path(userRegistration.username()).build();
    }

    /**
     * If these are static properties (factory and clients) and assigned with @BeforeAll, the setup is a little faster,
     * but a session might time out before all the tests are run, leading to flaky tests as the
     * timeouts get shorter. So it's really safest to use a separate security context for each test.
     */
    @BeforeEach
    public void setup(@Autowired Environment env, @Autowired RestClient.Builder builder) {

        var apiUriBuilder = baseUribuilder.builder().path("/api");

        // we get the rest client builder as configured for the app, including mappers
        clientFactory = new RestClientFactory(builder, apiUriBuilder.build());

        var loginBuilder = baseUribuilder.builder().path("/api").path("/login");
        loginUri = loginBuilder.build();
        loginRememberMeFalseUri = loginBuilder.replaceQueryParam("remember-me", "false").build();
        loginRememberMeTrueUri =  loginBuilder.replaceQueryParam("remember-me", "true").build();
        logoutUri = baseUribuilder.builder().path("/api").path("/logout").build();

        // See timeout values set in IntegrationTest, they configure the server so that we can time out here
        sessionTimeout = env.getProperty("spring.session.timeout", Duration.class);
        rememberMeTimeout = env.getProperty("app.security.rememberMe.tokenValidity", Duration.class);
    }

    @Test
    public void testUnauthenticatedCallFailsAndIsLogged() {

        // We don't return session tokens for unauthenticated calls, it is unnecessary.
        // Also, we don't want people to farm it for statistics on the cryptography of session tokens.

        // Attempt to access secured endpoint while unauthenticated
        var response = clientFactory.noLogin().get().uri(testUserInfoUri).retrieve().toEntity(String.class);

        assertResponse(response, 401, NOT_SET, NOT_SET);

        // all assertions should be done with test-specific information
        var url = testUserInfoUri.getPath();
        assertEquals(1, memoryAppender.search("InsufficientAuthenticationException", url).size());
    }

    @Test
    public void testUnauthenticatedLoginCreatesNoSessions() {

        // We don't return session tokens for unauthenticated calls, it is unnecessary.
        // Also, we don't want people to farm it for statistics on the cryptography of session tokens.

        // Attempt to access secured endpoint while unauthenticated
        var response = clientFactory.noLogin().get().uri(loginRememberMeFalseUri).retrieve().toEntity(String.class);

        assertResponse(response, 401, NOT_SET, NOT_SET);
    }

    @Test
    public void testUnauthenticatedCallCreatesNoRememberMe() {

        // We don't return session tokens for unauthenticated calls, it is unnecessary.
        // Also, we don't want people to farm it for statistics on the cryptography of session tokens.

        // Attempt to access secured endpoint while unauthenticated
        var response = clientFactory.noLogin().get().uri(loginRememberMeTrueUri).retrieve().toEntity(String.class);

        assertResponse(response, 401, NOT_SET, NOT_SET);

        var persistentLogins = persistentLoginRepository.findAllPersistentLogins(testUserName);
        assertEquals(0, persistentLogins.size());
    }

    @Test
    public void testIncorrectPassword() {

        var badPassword = STR."\{testUserPassword}typo";
        var badCreds = Base64.getEncoder().encodeToString(STR."\{testUserName}:\{badPassword}".getBytes());

        // the Java HttpClient does not give any control over retry for failed auth,
        // it just keeps retrying until it hits the limit and throws an exception.
        // Here we control auth ourselves, so we can actually disable the auth retry mechanism.
        var response = clientFactory.fromHttpClient(clientFactory.noAuth())
                .get()
                .uri(loginUri)
                .header("Authorization", STR."Basic \{badCreds}")
                .retrieve()
                .toEntity(String.class);

        assertResponse(response, 401, NOT_SET, CLEARED);

        assertEquals(1, memoryAppender.search("AuthenticationFailureBadCredentialsEvent", testUserName).size());
    }

    @Test
    public void testLoginLogoutWithoutRememberMe() {

        // Login without remember-me
        var basicAuth = clientFactory.basicAuth(testUserName, testUserPassword);
        var response = clientFactory.fromHttpClient(basicAuth).get().uri(loginUri).retrieve().toEntity(String.class);
        assertResponse(response, 200, SET, NOT_SET);

        // on login no remember-me token should have been created
        var persistentLogins = persistentLoginRepository.findAllPersistentLogins(testUserName);
        assertEquals(0, persistentLogins.size());

        response = clientFactory.fromHttpClient(basicAuth).get().uri(logoutUri).retrieve().toEntity(String.class);
        assertResponse(response, 204, CLEARED, CLEARED);

        // on logout there was still no remember-me token
        persistentLogins = persistentLoginRepository.findAllPersistentLogins(testUserName);
        assertEquals(0, persistentLogins.size());

        assertEquals(1, memoryAppender.search("AuthenticationSuccessEvent", testUserName).size());
        assertEquals(1, memoryAppender.search("LogoutSuccessEvent", testUserName).size());
    }


    @Test
    public void testLoginLogoutWithRememberMe() {

        // Login without remember-me
        var basicAuth = clientFactory.basicAuth(testUserName, testUserPassword);
        var response = clientFactory.fromHttpClient(basicAuth).get().uri(loginRememberMeTrueUri).retrieve().toEntity(String.class);
        assertResponse(response, 200, SET, SET);

        // on login a remember-me token should have been created
        var persistentLogins = persistentLoginRepository.findAllPersistentLogins(testUserName);
        assertEquals(1, persistentLogins.size());

        response = clientFactory.fromHttpClient(basicAuth).get().uri(logoutUri).retrieve().toEntity(String.class);
        assertResponse(response, 204, CLEARED, CLEARED);

        // on logout show that the remember-me token was removed from the server
        persistentLogins = persistentLoginRepository.findAllPersistentLogins(testUserName);
        assertEquals(0, persistentLogins.size());

        assertEquals(1, memoryAppender.search("AuthenticationSuccessEvent", testUserName).size());
        assertEquals(1, memoryAppender.search("LogoutSuccessEvent", testUserName).size());
    }

    @Test
    public void testSessionCookieTimeoutWithoutRememberMe() {

        // Login without remember-me, and access secured endpoint
        var basicAuth = clientFactory.basicAuth(testUserName, testUserPassword);
        var response = clientFactory.fromHttpClient(basicAuth).get().uri(loginRememberMeFalseUri).retrieve().toEntity(String.class);
        assertResponse(response, 200, SET, NOT_SET);

        // Session cookie is set in first response, no cookie in second response
        var sessionAuth = clientFactory.removeBasicAuth(basicAuth);
        var originalCookie = getCookie(response, SESSION_COOKIE);
        response = clientFactory.fromHttpClient(sessionAuth).get().uri(testUserInfoUri).retrieve().toEntity(String.class);
        assertResponse(response, 200, NOT_SET, NOT_SET);

        // on login a remember-me token should not have been created
        var persistentLogins = persistentLoginRepository.findAllPersistentLogins(testUserName);
        assertEquals(0, persistentLogins.size());

        waitForExpiration(sessionTimeout);

        // Then attempt to access secured endpoint
        // Result is a 401, Session is invalid, no session cookie is returned
        response = clientFactory.fromHttpClient(sessionAuth).get().uri(testUserInfoUri).retrieve().toEntity(String.class);
        assertResponse(response, 401, NOT_SET, NOT_SET);

        // on logout show that the remember-me token should still not be there
        persistentLogins = persistentLoginRepository.findAllPersistentLogins(testUserName);
        assertEquals(0, persistentLogins.size());

        // Check that the log message was generated
        var expiredCookie = originalCookie.getValue();
        assertEquals(1, memoryAppender.search("Session expired or invalid", expiredCookie, testUserName).size());
    }

    @Test
    public void testSessionCookieTimeoutWithRememberMe() {

        // the user has no persistent login record before logging in
        var beforeLogins = persistentLoginRepository.findAllPersistentLogins(testUserName);
        assertEquals(0, beforeLogins.size());

        // Login with remember-me and access secured endpoint
        // Result is a 200, Session cookie is set and remember-me cookie is set
        var basicAuth = clientFactory.basicAuth(testUserName, testUserPassword);
        var response = clientFactory.fromHttpClient(basicAuth).get().uri(loginRememberMeTrueUri).retrieve().toEntity(String.class);
        var originalSessionCookie = getCookie(response, SESSION_COOKIE);
        var originalRememberMeCookie = getCookie(response, REMEMBER_ME_COOKIE);
        assertResponse(response, 200, SET, SET);

        // on login, a database record for the persistent login is created
        var originalPersistentLogins = persistentLoginRepository.findAllPersistentLogins(testUserName);
        assertEquals(1, originalPersistentLogins.size());

        // subsequent requests should NOT have session cookie set, cookie is sent in subsequent requests
        var sessionAuth = clientFactory.removeBasicAuth(basicAuth);
        response = clientFactory.fromHttpClient(sessionAuth).get().uri(testUserInfoUri).retrieve().toEntity(String.class);
        assertResponse(response, 200, NOT_SET, NOT_SET);

        // Wait for session timeout but don't let remember-me time out
        waitForExpiration(sessionTimeout);

        // Then attempt to access secured endpoint again
        response = clientFactory.fromHttpClient(sessionAuth).get().uri(testUserInfoUri).retrieve().toEntity(String.class);
        assertResponse(response, 200, SET, SET);
        var newSessionCookie = getCookie(response, SESSION_COOKIE);
        var newRememberMeCookie = getCookie(response, REMEMBER_ME_COOKIE);

        // New session cookie is issued, new remember-me cookie is issued
        assertNotEquals(originalSessionCookie.getValue(), newSessionCookie.getValue());
        assertNotEquals(originalRememberMeCookie.getValue(), newRememberMeCookie.getValue());

        // persistent login record with remember-me cookie is updated
        var newPersistentLogins = persistentLoginRepository.findAllPersistentLogins(testUserName);
        assertEquals(1, newPersistentLogins.size());

        // on refresh, the token in the existing database record is replaced and the lastUsed is updated
        var originalPersistentLogin = originalPersistentLogins.stream().collect(toExactlyOne());
        var newPersistentLogin = newPersistentLogins.stream().collect(toExactlyOne());
        assertTrue(originalPersistentLogin.lastUsed().isBefore(newPersistentLogin.lastUsed()));
        assertEquals(originalPersistentLogin.series(), newPersistentLogin.series());
        assertEquals(originalPersistentLogin.username(), newPersistentLogin.username());
        assertNotEquals(originalPersistentLogin.token(), newPersistentLogin.token());

        // Check that the log message was generated
        assertEquals(2, memoryAppender.search("Authentication event AuthenticationSuccessEvent", testUserName).size());
        assertEquals(1, memoryAppender.search("Authentication event InteractiveAuthenticationSuccessEvent", testUserName).size());
    }

    @Test
    public void testRememberMeCookieTimeout() {

        // Login with remember-me and access secured endpoint
        // Result is a 200, Session cookie is set and remember-me cookie is set
        var basicAuth = clientFactory.basicAuth(testUserName, testUserPassword);
        var response = clientFactory.fromHttpClient(basicAuth).get().uri(loginRememberMeTrueUri).retrieve().toEntity(String.class);
        assertResponse(response, 200, SET, SET);

        // on login, a database record for the persistent login is created
        var persistentLogins = persistentLoginRepository.findAllPersistentLogins(testUserName);
        assertEquals(1, persistentLogins.size());

        // subsequent requests should NOT have session cookie set, cookie is sent in subsequent requests
        var sessionAuth = clientFactory.removeBasicAuth(basicAuth);
        response = clientFactory.fromHttpClient(sessionAuth).get().uri(testUserInfoUri).retrieve().toEntity(String.class);
        assertResponse(response, 200, NOT_SET, NOT_SET);

        // Wait for remember-me timeout
        waitForExpiration(rememberMeTimeout);

        // after remember-me timeout, the remember-me token on the server is not deleted
        persistentLogins = persistentLoginRepository.findAllPersistentLogins(testUserName);
        assertEquals(1, persistentLogins.size());

        // for testing, we can indiscriminately delete all expired tokens, not just for this user
        // expired tokens for other users are useless anyway (unless it affects cookies being returned as NOT_SET vs CLEARED)
        schedulingService.callDeleteExpiredRememberMeTokens();

        // the token shouldn't even be present in the database anymore
        persistentLogins = persistentLoginRepository.findAllPersistentLogins(testUserName);
        assertEquals(0, persistentLogins.size());

        // attempt to access secured endpoint again
        // Result is a 401, Session is invalid, no session cookie is returned, remember-me cookie is cleared
        response = clientFactory.fromHttpClient(sessionAuth).get().uri(testUserInfoUri).retrieve().toEntity(String.class);
        assertResponse(response, 401, NOT_SET, CLEARED);

        // once remember me cookie is cleared, any subsequent response will not try to set either cookie
        response = clientFactory.fromHttpClient(sessionAuth).get().uri(testUserInfoUri).retrieve().toEntity(String.class);
        assertResponse(response, 401, NOT_SET, NOT_SET);

        // Check that the log message was generated
        assertEquals(1, memoryAppender.search("Authentication event AuthenticationSuccessEvent", testUserName).size());
        assertEquals(2, memoryAppender.search("Session expired or invalid.", "SESSION", testUserName).size());
        assertEquals(1, memoryAppender.search("Session expired or invalid.", "remember-me", testUserName).size());
    }

    private void waitForExpiration(Duration timeout) {
        try {
            Thread.sleep(timeout.toMillis());
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpCookie getCookie(ResponseEntity<String> response, String cookieName) {
        return findCookie(response, cookieName).get();
    }

    private void assertResponse(ResponseEntity<String> response, int expectedStatusCode, CookieResponse expectedSession, CookieResponse expectedRememberMe) {
        assertEquals(expectedStatusCode, response.getStatusCode().value());
        assertEquals(expectedSession, getCookieResponse(response, SESSION_COOKIE));
        assertEquals(expectedRememberMe, getCookieResponse(response, REMEMBER_ME_COOKIE));
    }

    private CookieResponse getCookieResponse(ResponseEntity<String> response, String cookieName) {
        var cookieVal = findCookie(response, cookieName).map(HttpCookie::getValue);
        var cookieState = cookieVal.isPresent() ? SET : NOT_SET;
        return cookieState == SET && cookieVal.filter(String::isEmpty).isPresent() ? CLEARED : cookieState;
    }

    private Optional<HttpCookie> findCookie(ResponseEntity<String> response, String cookieName) {
        return response.getHeaders().getOrDefault("SET-COOKIE", new ArrayList<>())
                .stream()
                .map(HttpCookie::parse)
                .flatMap(List::stream)
                .filter(c -> c.getName().equals(cookieName))
                .collect(toOne());
    }
}
