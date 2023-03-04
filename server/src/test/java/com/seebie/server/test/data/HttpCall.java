package com.seebie.server.test.data;

import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.util.CollectionUtils.unmodifiableMultiValueMap;

/**
 * Could this be replaced with Java's own HttpRequest?
 * WebMvcTest uses MultiValueMap for request parameters,
 * but HttpRequest only handles request parameters as embedded in the URI
 */
public record HttpCall(HttpMethod httpMethod, String url, Object reqBody, MultiValueMap<String, String> reqParams) {

    public static final MultiValueMap<String, String> NO_PARAM = unmodifiableMultiValueMap(new LinkedMultiValueMap<>());

    public HttpCall withParam(String name, String value) {
        // we don't need to url encode the parameters here
        var newParams = new LinkedMultiValueMap<String, String>();
        newParams.putAll(reqParams);
        newParams.put(name, List.of(value));
        return new HttpCall(httpMethod, url, reqBody, newParams);
    }

    public static HttpCall post(String url, Object reqBody) {
        return new HttpCall(HttpMethod.POST, url, reqBody, NO_PARAM);
    }

    public static HttpCall put(String url, Object reqBody) {
        return new HttpCall(HttpMethod.PUT, url, reqBody, NO_PARAM);
    }

    public static HttpCall get(String url) {
        return new HttpCall(GET, url, "", NO_PARAM);
    }

    public static HttpCall delete(String url) {
        return new HttpCall(HttpMethod.DELETE, url, "", NO_PARAM);
    }

    public HttpRequest toRequest(String base) throws URISyntaxException {
        return HttpRequest.newBuilder()
                .GET()
                .uri(new URI(base + url() + reqParams(reqParams())))
                .build();
    }

    private static String reqParams(MultiValueMap<String, String> params) {
        var paramStart = params.isEmpty() ? "" : "?";
        return paramStart + params.entrySet().stream()
                .map(entry -> reqParamPairs(entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("&"));
    }

    private static String reqParamPairs(String key, List<String> values) {
        return values.stream()
                .map(value -> key + "=" + value)
                .collect(Collectors.joining("&"));
    }

}
