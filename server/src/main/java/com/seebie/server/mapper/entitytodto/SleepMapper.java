package com.seebie.server.mapper.entitytodto;

import com.seebie.server.dto.SleepData;
import com.seebie.server.entity.SleepSession;
import com.seebie.server.entity.Tag;
import org.springframework.stereotype.Component;

import java.util.function.Function;

import static java.util.stream.Collectors.toSet;

@Component
public class SleepMapper implements Function<SleepSession, SleepData> {

    @Override
    public SleepData apply(SleepSession entity) {

        var tags = entity.getTags().stream().map(Tag::getText).collect(toSet());
        var dto = new SleepData(entity.getNotes(), entity.getOutOfBed(),
                tags, entity.getStartTime(), entity.getStopTime());

        return dto;
    }
}
