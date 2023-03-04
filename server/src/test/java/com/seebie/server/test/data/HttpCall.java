package com.seebie.server.test.data;

import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.util.CollectionUtils.unmodifiableMultiValueMap;


public record HttpCall(HttpMethod httpMethod, String url, Object reqBody, MultiValueMap<String, String> reqParams) {

    public static final MultiValueMap<String, String> NO_PARAM = unmodifiableMultiValueMap(new LinkedMultiValueMap<>());

    /**
     * Works the same as HttpRequest.headers()
     * Do not need to url encode the parameters.
     * @param parameters
     * @return
     */
    public HttpCall params(String... parameters) {
        var newParams = new LinkedMultiValueMap<String, String>();
        newParams.putAll(reqParams);
        for(int i=0; i < parameters.length; i+=2) {
            newParams.add(parameters[i], parameters[i+1]);
        }
        return new HttpCall(httpMethod, url, reqBody, newParams);
    }

    public static HttpCall post(String url, Object reqBody) {
        return new HttpCall(POST, url, reqBody, NO_PARAM);
    }

    public static HttpCall put(String url, Object reqBody) {
        return new HttpCall(PUT, url, reqBody, NO_PARAM);
    }

    public static HttpCall get(String url) {
        return new HttpCall(GET, url, "", NO_PARAM);
    }

    public static HttpCall delete(String url) {
        return new HttpCall(DELETE, url, "", NO_PARAM);
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
