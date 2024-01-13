package com.seebie.server.test.data;

import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.util.CollectionUtils.unmodifiableMultiValueMap;

public record Request(HttpMethod httpMethod, String url, Object reqBody, MultiValueMap<String, String> reqParams) {

    public static final MultiValueMap<String, String> NO_PARAM = unmodifiableMultiValueMap(new LinkedMultiValueMap<>());

    public Request() {
        this(GET, "", "", NO_PARAM);
    }

    public Request method(HttpMethod newHttpMethod) {
        return new Request(newHttpMethod, url, reqBody, reqParams);
    }

    public Request url(String newUrl) {
        return new Request(httpMethod, newUrl, reqBody, reqParams);
    }

    public Request body(Object newReqBody) {
        return new Request(httpMethod, url, newReqBody, reqParams);
    }

    /**
     * Works the same as HttpRequest.headers()
     * Do not need to url encode the parameters.
     *
     * @param newReqParams
     * @return
     */
    public Request params(String... newReqParams) {
        if(newReqParams.length % 2 != 0) {
            throw new IllegalArgumentException("Number of args must be even");
        }
        var newParams = new LinkedMultiValueMap<>(reqParams);
        for (int i = 0; i < newReqParams.length; i += 2) {
            newParams.add(newReqParams[i], newReqParams[i + 1]);
        }
        return new Request(httpMethod, url, reqBody, unmodifiableMultiValueMap(newParams));
    }

}
