package com.seebie.server.test.data;

import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.util.CollectionUtils.unmodifiableMultiValueMap;

public record Request(HttpMethod httpMethod, String url, Object reqBody, MultiValueMap<String, String> reqParams) {

    public static final MultiValueMap<String, String> NO_PARAM = unmodifiableMultiValueMap(new LinkedMultiValueMap<>());

    public static Request post(String urlPath, Object reqBody) {
        return new Request(POST, urlPath, reqBody, NO_PARAM);
    }

    public static Request put(String urlPath, Object reqBody) {
        return new Request(PUT, urlPath, reqBody, NO_PARAM);
    }

    public static Request get(String urlPath, String[] requestParams) {
        return new Request(GET, urlPath, "", toParams(requestParams));
    }

    public static Request get(String urlPath) {
        return new Request( GET, urlPath, "", NO_PARAM);
    }

    public static Request delete(String urlPath) {
        return new Request(DELETE, urlPath, "", NO_PARAM);
    }

    /**
     * Works the same as HttpRequest.headers()
     * Do not need to url encode the parameters.
     *
     * @param newReqParams
     * @return
     */
    public static MultiValueMap<String, String> toParams(String... newReqParams) {
        if(newReqParams.length % 2 != 0) {
            throw new IllegalArgumentException("Number of args must be even");
        }
        var newParams = new LinkedMultiValueMap<String, String>();
        for (int i = 0; i < newReqParams.length; i += 2) {
            newParams.add(newReqParams[i], newReqParams[i + 1]);
        }
        return unmodifiableMultiValueMap(newParams);
    }

}
