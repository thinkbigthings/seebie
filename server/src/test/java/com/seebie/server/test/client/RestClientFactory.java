package com.seebie.server.test.client;

import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.net.CookieManager;
import java.net.http.HttpClient;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;

public class RestClientFactory {

    private final RestClient.Builder restClientBuilder;

    private static final SSLContext insecureContext = createInsecureSsl();

    public RestClientFactory(RestClient.Builder restClientBuilder, int randomPort) {

        // would like to customize the builder at system startup time
        // with a RestClientCustomizer or ObjectProvider<RestClientCustomizer>
        // but couldn't find out to wire it

        var baseUrl = STR."https://localhost:\{randomPort}";
        this.restClientBuilder = restClientBuilder.clone().baseUrl(baseUrl);
    }

    public RestClient createUnAuthClient() {
        return restClientBuilder.clone()
                .requestFactory(new JdkClientHttpRequestFactory(unAuthClient()))
                .build();
    }

    public RestClient createLoggedInClient(String username, String plainTextPassword) {

            // I thought the existing ssl bundle would work to configure ssl here,
            // but whether I use it to create an SslContext or use RestClient.Builder.apply(ssl.fromBundle("appbundle"))
            // it fails with "unable to find valid certification path to requested target"
            // so need to use an insecure truststore to work with self-signed certs
            var basicAuth = basicAuth(username, plainTextPassword);
            var basicRestClient = restClientBuilder.clone()
                    .requestFactory(new JdkClientHttpRequestFactory(basicAuth))
                    .build();

            basicRestClient.get()
                    .uri("/api/login")
                    .retrieve()
                    .body(String.class);

            var sessionAuth = removeBasicAuth(basicAuth);
            return restClientBuilder.clone()
                    .requestFactory(new JdkClientHttpRequestFactory(sessionAuth))
                    .build();
    }

    private static HttpClient unAuthClient() {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(300))
                .cookieHandler(new CookieManager())
                .sslContext(insecureContext)
                .build();
    }

    private static HttpClient basicAuth(String username, String password) {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(300))
                .cookieHandler(new CookieManager())
                .authenticator(new BasicAuthenticator(username, password))
                .sslContext(insecureContext)
                .build();
    }

    private static HttpClient removeBasicAuth(HttpClient client) {
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
