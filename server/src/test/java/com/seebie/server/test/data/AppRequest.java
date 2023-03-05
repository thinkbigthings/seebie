package com.seebie.server.test.data;

import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.HttpMethod.DELETE;
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

    public AppRequest urlPrefix(String prefix) {
        return new AppRequest(httpMethod, prefix+url, reqBody, reqParams);
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
        var newParams = new LinkedMultiValueMap<String, String>();
        newParams.putAll(reqParams);
        for (int i = 0; i < newReqParams.length; i += 2) {
            newParams.add(newReqParams[i], newReqParams[i + 1]);
        }
        return new AppRequest(httpMethod, url, reqBody, unmodifiableMultiValueMap(newParams));
    }

    public static AppRequest post(String url, Object reqBody) {
        return new AppRequest().method(POST).url(url).body(reqBody);
    }

    public static AppRequest put(String url, Object reqBody) {
        return new AppRequest().method(PUT).url(url).body(reqBody);
    }

    public static AppRequest get(String url) {
        return new AppRequest().method(GET).url(url);
    }

    public static AppRequest delete(String url) {
        return new AppRequest().method(DELETE).url(url);
    }
}
