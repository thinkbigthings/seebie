package com.seebie.server.test.data;

import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.function.Function;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;

public class MvcRequestMapper implements Function<AppRequest, MockHttpServletRequestBuilder> {

    private Function<Object, String> mapper;

    public MvcRequestMapper(Function<Object, String> mapper) {
        this.mapper = mapper;
    }

    @Override
    public MockHttpServletRequestBuilder apply(AppRequest request) {
        return request(request.httpMethod(), request.url())
                .content(mapper.apply(request.reqBody()))
                .params(request.reqParams())
                .contentType(APPLICATION_JSON)
                .secure(true);
    }
}
