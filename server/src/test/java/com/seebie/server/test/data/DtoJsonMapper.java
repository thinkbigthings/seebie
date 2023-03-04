package com.seebie.server.test.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seebie.server.dto.PersonalInfo;
import com.seebie.server.dto.RegistrationRequest;
import com.seebie.server.dto.SleepData;

import java.util.function.Function;

public class DtoJsonMapper implements Function<Object, String> {

    private ObjectMapper mapper;

    public DtoJsonMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public String apply(Object dto) {
        try {
            return switch (dto) {
                case String s -> s;
                case PersonalInfo p -> mapper.writerFor(p.getClass()).writeValueAsString(p);
                case RegistrationRequest r -> mapper.writerFor(r.getClass()).writeValueAsString(r);
                case SleepData d -> mapper.writerFor(d.getClass()).writeValueAsString(d);
                default -> throw new IllegalArgumentException("Can't find object mapper for " + dto);
            };
        }
        catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Can't write value for " + dto);
        }
    }
}
