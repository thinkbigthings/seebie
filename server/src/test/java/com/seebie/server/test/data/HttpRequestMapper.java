package com.seebie.server.test.data;

import org.springframework.util.MultiValueMap;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class HttpRequestMapper  implements Function<AppRequest, HttpRequest> {

    private Function<Object, String> mapper;

    public HttpRequestMapper(Function<Object, String> mapper) {
        this.mapper = mapper;
    }

    @Override
    public HttpRequest apply(AppRequest request) {
        try {
            var uri = new URI(request.url() + parseParams(request.reqParams()));
            var builder = HttpRequest.newBuilder().uri(uri);
            var httpMethod = request.httpMethod().name();

            switch(httpMethod) {
                case "GET" -> builder.GET();
                case "POST" -> builder.POST(HttpRequest.BodyPublishers.ofString(mapper.apply(request.reqBody())));
                case "PUT" -> builder.PUT(HttpRequest.BodyPublishers.ofString(mapper.apply(request.reqBody())));
                case "DELETE" -> builder.DELETE();
                default -> throw new IllegalArgumentException("Can't create request for method " + httpMethod);
            }

            return builder.build();
        }
        catch (URISyntaxException e) {
            throw new IllegalArgumentException("Can't parse URI " + request.url());
        }

    }

    private String parseParams(MultiValueMap<String, String> params) {
        var paramStart = params.isEmpty() ? "" : "?";
        return paramStart + params.entrySet().stream()
                .map(entry -> reqParamPairs(entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("&"));
    }

    private String reqParamPairs(String key, List<String> values) {
        return values.stream()
                .map(value -> key + "=" + value)
                .collect(Collectors.joining("&"));
    }

}
