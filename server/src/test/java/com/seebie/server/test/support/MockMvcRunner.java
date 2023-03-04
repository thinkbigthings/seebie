package com.seebie.server.test.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seebie.server.dto.PersonalInfo;
import com.seebie.server.dto.RegistrationRequest;
import com.seebie.server.dto.SleepData;
import com.seebie.server.test.data.HttpCall;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MockMvcRunner {

    private MockMvc mvc;
    private ObjectMapper mapper;

    public MockMvcRunner(MockMvc mvc, MappingJackson2HttpMessageConverter converter) {
        this.mvc = mvc;
        this.mapper = converter.getObjectMapper();
    }

    public void test(HttpCall test, int expectedStatus) throws Exception {
        mvc.perform(request(test.httpMethod(), test.url())
                        .content(toJson(test.reqBody()))
                        .params(test.reqParams())
                        .contentType(APPLICATION_JSON)
                        .secure(true))
                .andDo(print())
                .andExpect(status().is(expectedStatus));
    }

    public String toJson(Object requestBody) throws JsonProcessingException {
        return switch (requestBody) {
            case String s -> s;
            case PersonalInfo p -> mapper.writerFor(p.getClass()).writeValueAsString(p);
            case RegistrationRequest r -> mapper.writerFor(r.getClass()).writeValueAsString(r);
            case SleepData d -> mapper.writerFor(d.getClass()).writeValueAsString(d);
            default -> throw new IllegalStateException("Can't create request body for " + requestBody);
        };
    }
}
