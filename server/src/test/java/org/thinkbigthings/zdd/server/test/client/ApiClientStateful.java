package org.thinkbigthings.zdd.server.test.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.IOException;
import java.net.CookieManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;

import static java.lang.System.lineSeparator;

public class ApiClientStateful {

    record Header(String name, String value) {}

    public static final class MediaType {
        public static final String APPLICATION_JSON_VALUE = "application/json";
    }

    private final Header JSON_CONTENT = new Header("Content-Type", MediaType.APPLICATION_JSON_VALUE);
    private final ObjectMapper mapper = new ObjectMapper();

    private URI login;
    private URI logout;
    private HttpClient client;

    static {
        System.setProperty("jdk.internal.httpclient.disableHostnameVerification", Boolean.TRUE.toString());
    }

    public ApiClientStateful(String baseUrl, String username, String password) {
        this(URI.create(baseUrl), username, password);
    }

    public ApiClientStateful(URI base, String username, String password) {

        login = base.resolve("login");
        logout = base.resolve("logout");

        // HttpClient does not send Basic credentials until challenged for them with a WWW-Authenticate header from the server.
        // Further, the only type of challenge it understands is for Basic authentication.
        // However, this happens transparently from the caller's point of view.
        // So the workflow is: The Java HttpClient hits the login endpoint, receives a 401,
        // makes a second call with basic auth using the provided Authenticator,
        // and gets back cookies for auth (session and remember me token).
        client = basicAuthClient(username, password);
        get(login);

        // subsequent calls should use session and/or remember me token
        // remove the authorizor, otherwise it still adds the basic auth headers
        client = removeBasicAuth(client);
    }

    public void logout() {
        get(logout);
    }

    protected HttpClient basicAuthClient(String username, String password) {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(300))
                .cookieHandler(new CookieManager())
                .authenticator(new BasicAuthenticator(username, password))
                .sslContext(createSsl())
                .build();
    }

    protected HttpClient removeBasicAuth(HttpClient client) {
        return HttpClient.newBuilder()
                .connectTimeout(client.connectTimeout().get())
                .cookieHandler(client.cookieHandler().get())
                .sslContext(client.sslContext())
                .build();
    }

    protected SSLContext createSsl() {
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

    protected HttpRequest.Builder request(URI uri) {
        return HttpRequest.newBuilder()
                .uri(uri)
                .setHeader(JSON_CONTENT.name(), JSON_CONTENT.value());
    }

    public void put(URI uri, Object body) {
        send(request(uri).PUT(publisher(body)).build());
    }

    public HttpResponse<String> post(URI uri, Object body) {
        return send(request(uri).POST(publisher(body)).build());
    }

    public HttpResponse<String> getResponse(URI uri) {
        return send(request(uri).GET().build());
    }

    public String get(URI uri) {
        return getResponse(uri).body();
    }

    public <T> T get(URI uri, Class<T> jsonResponse) {

        return parse(get(uri), jsonResponse);
    }

    public HttpResponse<String> send(HttpRequest request) {

        try {

            if( ! request.uri().getScheme().equals("https")) {
                throw new RuntimeException("This client should use https because we are passing around auth info");
            }

            // more on body handlers here https://openjdk.java.net/groups/net/httpclient/recipes.html
            // might be fun to have direct-to-json-object body handler

//            Duration randomLatency = Duration.ofMillis(new Random().nextInt(15));
//            sleep(randomLatency);
            HttpResponse<String> response = throwOnError(client.send(request, HttpResponse.BodyHandlers.ofString()));
//            sleep(randomLatency);

//            String headerLog = response.headers().map().entrySet().stream()
//                    .map(entry -> entry.getKey() + ": " + String.join(", ", entry.getValue()))
//                    .collect(Collectors.joining(lineSeparator()));
//
//            System.out.println(request.uri().getPath());
//            System.out.println(headerLog);
//            System.out.println();

            return response;
        }
        catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }

    }

    public <T> T parse(String json, Class<T> type) {
        try {
            return mapper.readValue(json, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public HttpRequest.BodyPublisher publisher(Object object) {

        try {

            String json = object instanceof String
                    ? object.toString()
                    : mapper.writeValueAsString(object);

            return HttpRequest.BodyPublishers.ofString(json);
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public HttpResponse<String> throwOnError(HttpResponse<String> response) {

        if(response.statusCode() < 200 || response.statusCode() >= 300) {
            String message = "Return status code was " + response.statusCode();
            message += " in call to " + response.request().uri() + lineSeparator();
            message += " with response headers " + response.headers().map() + lineSeparator();
            message += " with response body " + response.body();
            throw new RuntimeException(message);
        }

        return response;
    }


    private void sleep(Duration sleepDuration) {
        if(sleepDuration.isZero()) {
            return;
        }
        try {
            Thread.sleep(sleepDuration.toMillis());
        }
        catch(InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
