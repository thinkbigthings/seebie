package com.seebie.server.mapper.entitytodto;

import com.seebie.server.dto.SleepDetails;

import java.util.List;
import java.util.function.Function;

import static com.seebie.server.mapper.entitytodto.ZonedDateTimeConverter.format;

public class SleepDetailsToCsvRow implements Function<SleepDetails, List<String>> {

    @Override
    public List<String> apply(SleepDetails details) {

        var data = details.sleepData();

        return List.of(
                        format(data.startTime()),
                        format(data.stopTime()),
                        data.zoneId(),
                        Integer.toString(details.minutesAsleep()),
                        Integer.toString(data.minutesAwake()),
                        data.notes()
                    );
    }
}
