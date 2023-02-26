package com.seebie.server.test.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seebie.server.dto.PersonalInfo;
import com.seebie.server.dto.RegistrationRequest;
import com.seebie.server.dto.SleepData;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MvcTestRunner {

    private ObjectMapper mapper;

    public MvcTestRunner(MappingJackson2HttpMessageConverter converter) {
        mapper = converter.getObjectMapper();
    }

    public void test(MockMvc mvc, HttpMethod method, String url, Object body, int expectedStatus) throws Exception {
        mvc.perform(request(method, url)
                        .content(toJson(body))
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
