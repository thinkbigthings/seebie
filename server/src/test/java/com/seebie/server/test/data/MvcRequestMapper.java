package com.seebie.server.test.data;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.RequestBuilder;

import java.util.function.Function;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;

public class MvcRequestMapper implements Function<Request, RequestBuilder> {

    private Function<Object, String> mapper;

    public MvcRequestMapper(Function<Object, String> mapper) {
        this.mapper = mapper;
    }

    @Override
    public RequestBuilder apply(Request request) {

            return request.reqBody() instanceof MockMultipartFile multipartFile
                    ? multipart(request.url())
                        .file(multipartFile)
                        .secure(true)
                    : request(request.httpMethod(), request.url())
                        .content(mapper.apply(request.reqBody()))
                        .params(request.reqParams())
                        .contentType(APPLICATION_JSON)
                        .secure(true);
    }
}
