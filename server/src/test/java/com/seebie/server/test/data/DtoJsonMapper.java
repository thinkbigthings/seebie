package com.seebie.server.test.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.function.Function;

/**
 * The test request object takes a string, so we need to format objects ahead of time
 * to send a request body into the app.
 */
public record DtoJsonMapper(ObjectMapper mapper) implements Function<Object, String> {

    @Override
    public String apply(Object dto) {

        try {
            return mapper.writerFor(dto.getClass()).writeValueAsString(dto);
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException("Can't write value for " + dto, e);
        }
    }
}
