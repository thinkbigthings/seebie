package com.seebie.server.test.data;

import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.util.CollectionUtils.unmodifiableMultiValueMap;

public record AppRequest(HttpMethod httpMethod, String url, Object reqBody, MultiValueMap<String, String> reqParams) {

    public static final MultiValueMap<String, String> NO_PARAM = unmodifiableMultiValueMap(new LinkedMultiValueMap<>());

    public AppRequest() {
        this(GET, "", "", NO_PARAM);
    }

    public AppRequest method(HttpMethod newHttpMethod) {
        return new AppRequest(newHttpMethod, url, reqBody, reqParams);
    }

    public AppRequest url(String newUrl) {
        return new AppRequest(httpMethod, newUrl, reqBody, reqParams);
    }

    public AppRequest body(Object newReqBody) {
        return new AppRequest(httpMethod, url, newReqBody, reqParams);
    }

    /**
     * Works the same as HttpRequest.headers()
     * Do not need to url encode the parameters.
     *
     * @param newReqParams
     * @return
     */
    public AppRequest params(String... newReqParams) {
        if(newReqParams.length % 2 != 0) {
            throw new IllegalArgumentException("Number of args must be even");
        }
        var newParams = new LinkedMultiValueMap<>(reqParams);
        for (int i = 0; i < newReqParams.length; i += 2) {
            newParams.add(newReqParams[i], newReqParams[i + 1]);
        }
        return new AppRequest(httpMethod, url, reqBody, unmodifiableMultiValueMap(newParams));
    }

}
