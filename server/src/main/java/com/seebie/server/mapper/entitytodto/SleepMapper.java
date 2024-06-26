package com.seebie.server.mapper.entitytodto;

import com.seebie.server.dto.SleepDetails;
import com.seebie.server.entity.SleepSession;

import java.util.function.Function;

public class SleepMapper implements Function<SleepSession, SleepDetails> {

    @Override
    public SleepDetails apply(SleepSession entity) {
        return new SleepDetails(entity.getId(), entity.getMinutesAsleep(), entity.getNotes(), entity.getMinutesAwake(),
                entity.getStartTime(), entity.getStopTime(), entity.getZoneId().getId());
    }
}
