package com.seebie.server.mapper.entitytodto;

import com.seebie.server.dto.SleepDetails;
import com.seebie.server.entity.SleepSession;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.function.Function;

public class SleepMapper implements Function<SleepSession, SleepDetails> {

    @Override
    public SleepDetails apply(SleepSession entity) {
        return new SleepDetails(entity.getId(), entity.getMinutesAsleep(), entity.getNotes(), entity.getMinutesAwake(),
                ZonedDateTime.of(entity.getStartTime(), ZoneId.of(entity.getZoneId())),
                ZonedDateTime.of(entity.getStopTime(), ZoneId.of(entity.getZoneId())),
                entity.getZoneId());
    }
}
