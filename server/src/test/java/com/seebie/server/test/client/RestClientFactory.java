package com.seebie.server.test.client;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.JdkClientHttpRequestFactory;
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

import static com.seebie.server.security.WebSecurityConfig.API_LOGIN;

public class RestClientFactory {

    private final RestClient.Builder restClientBuilder;

    private static final SSLContext insecureContext = createInsecureSsl();

    public RestClientFactory(RestClient.Builder restClientBuilder, URI baseUrl) {

        // would like to customize the builder at system startup time
        // with a RestClientCustomizer or ObjectProvider<RestClientCustomizer>
        // but couldn't find out how to wire it

        this.restClientBuilder = restClientBuilder.clone()
                .defaultStatusHandler(HttpStatusCode::is4xxClientError, (request, resp) -> {}) // don't throw exceptions on 4XX errors
                .baseUrl(baseUrl.toString());
    }

    public RestClient noLogin() {
        return fromHttpClient(noAuth());
    }

    public RestClient login(String email, String plainTextPassword) {

            var basicAuth = basicAuth(email, plainTextPassword);

            var response = fromHttpClient(basicAuth).get().uri(API_LOGIN).retrieve().body(String.class);

            // subsequent calls should use session and/or remember me token
            // remove the authorizor, otherwise it still adds the basic auth headers
            return fromHttpClient(removeBasicAuth(basicAuth));
    }

    public RestClient fromHttpClient(HttpClient httpClient) {
        return restClientBuilder.clone()
                .requestFactory(new JdkClientHttpRequestFactory(httpClient))
                .build();
    }

    public static HttpClient noAuth() {
        return baseBuilder().build();
    }

    public HttpClient basicAuth(String username, String password) {
        return baseBuilder().authenticator(new BasicAuthenticator(username, password)).build();
    }

    private static HttpClient.Builder baseBuilder() {

        // I thought the existing ssl bundle would work to configure ssl here,
        // but whether I use it to create an SslContext or use RestClient.Builder.apply(ssl.fromBundle("appbundle"))
        // it fails with "unable to find valid certification path to requested target"
        // so need to use an insecure truststore to work with self-signed certs
        return HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NEVER)
                .connectTimeout(Duration.ofSeconds(300))
                .cookieHandler(new CookieManager())
                .sslContext(insecureContext);
    }

    public HttpClient removeBasicAuth(HttpClient client) {
        return HttpClient.newBuilder()
                .connectTimeout(client.connectTimeout().get())
                .cookieHandler(client.cookieHandler().get())
                .sslContext(client.sslContext())
                .build();
    }

    private static SSLContext createInsecureSsl() {
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

}
