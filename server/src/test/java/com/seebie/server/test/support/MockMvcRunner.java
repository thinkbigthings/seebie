package com.seebie.server.test.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seebie.server.dto.PersonalInfo;
import com.seebie.server.dto.RegistrationRequest;
import com.seebie.server.dto.SleepData;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.util.CollectionUtils.unmodifiableMultiValueMap;

public class MockMvcRunner {

    public record EndpointTest(HttpMethod httpMethod, String url, Object reqBody, MultiValueMap<String, String> reqParams) {

        public static final MultiValueMap<String,String> NO_PARAM = unmodifiableMultiValueMap(new LinkedMultiValueMap<>());

        public static EndpointTest post(String url, Object reqBody) {
            return new EndpointTest(HttpMethod.POST, url, reqBody, NO_PARAM);
        }
        public static EndpointTest put(String url, Object reqBody) {
            return new EndpointTest(HttpMethod.PUT, url, reqBody, NO_PARAM);
        }
        public static EndpointTest get(String url) {
            return new EndpointTest(HttpMethod.GET, url, "", NO_PARAM);
        }
        public static EndpointTest get(String url, MultiValueMap<String, String> reqParams) {
            return new EndpointTest(HttpMethod.GET, url, "", reqParams);
        }
        public static EndpointTest delete(String url) {
            return new EndpointTest(HttpMethod.DELETE, url, "", NO_PARAM);
        }
    }

    private MockMvc mvc;
    private ObjectMapper mapper;

    public MockMvcRunner(MockMvc mvc, MappingJackson2HttpMessageConverter converter) {
        this.mvc = mvc;
        this.mapper = converter.getObjectMapper();
    }

    public void test(EndpointTest test, int expectedStatus) throws Exception {
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
