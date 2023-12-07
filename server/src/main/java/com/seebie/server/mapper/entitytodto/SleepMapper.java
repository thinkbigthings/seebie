package com.seebie.server.mapper.entitytodto;

import com.seebie.server.dto.SleepData;
import com.seebie.server.entity.SleepSession;

import java.util.function.Function;

public class SleepMapper implements Function<SleepSession, SleepData> {

    @Override
    public SleepData apply(SleepSession entity) {

        var dto = new SleepData(entity.getNotes(), entity.getMinutesAwake(),
                entity.getStartTime(), entity.getStopTime(), entity.getZoneId().getId());

        return dto;
    }
}
