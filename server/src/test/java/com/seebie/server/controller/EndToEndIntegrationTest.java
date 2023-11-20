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
import com.seebie.server.test.client.BasicAuthenticator;
import com.seebie.server.test.client.InsecureTrustManager;
import com.seebie.server.test.client.ParsablePage;
import com.seebie.server.test.data.TestData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.client.RestClientSsl;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClient;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.net.CookieManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
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

    public static HttpClient unAuthClient(SSLContext sslContext) {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(300))
                .cookieHandler(new CookieManager())
                .sslContext(sslContext)
                .build();
    }

    public static HttpClient basicAuth(String username, String password, SSLContext sslContext) {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(300))
                .cookieHandler(new CookieManager())
                .authenticator(new BasicAuthenticator(username, password))
                .sslContext(sslContext)
                .build();
    }

    public static HttpClient removeBasicAuth(HttpClient client) {
        return HttpClient.newBuilder()
                .connectTimeout(client.connectTimeout().get())
                .cookieHandler(client.cookieHandler().get())
                .sslContext(client.sslContext())
                .build();
    }


    protected static SSLContext createSsl() {
        try {
            // don't check certificates so we can use self-signed certs
            SSLContext sc = SSLContext.getInstance("TLSv1.3");
            sc.init(null, new TrustManager[]{new InsecureTrustManager()}, new SecureRandom());
            return sc;
        }
        catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    public static void setupClient(@Autowired RestClient.Builder restClientBuilder, @LocalServerPort int randomPort) {

        var insecureContext = createSsl();

        // would like to customize it at startup with a RestClientCustomizer or ObjectProvider<RestClientCustomizer>
        // but couldn't find out to wire it
        var baseUrl = STR."https://localhost:\{randomPort}";
        restClientBuilder.baseUrl(baseUrl);


        // I thought the existing bundle would work to configure ssl here,
        // but it fails with "unable to find valid certification path to requested target"
        // whether I use it to create an SslContext or use RestClient.Builder.apply(ssl.fromBundle("appbundle"))
        var basicAuth = basicAuth("admin", "admin", insecureContext);

        var basicRestClient = restClientBuilder.clone()
                                            .requestFactory(new JdkClientHttpRequestFactory(basicAuth))
                                            .build();

        basicRestClient.get()
                .uri("/api/login")
                .retrieve()
                .body(String.class);


        var restClient = restClientBuilder.clone()
                                            .requestFactory(new JdkClientHttpRequestFactory(removeBasicAuth(basicAuth)))
                                            .build();

        var userResponse = restClient.get()
                .uri("/api/user/admin")
                .retrieve()
                .body(String.class);

        System.out.println("userResponse = " + userResponse);
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
