package com.seebie.server.mapper.entitytodto;

import com.seebie.server.dto.SleepData;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;

import static com.seebie.server.mapper.entitytodto.ZonedDateTimeToString.format;

@Component
public class SleepDataToRow implements Function<SleepData, List<String>> {

    @Override
    public List<String> apply(SleepData data) {

        return List.of(
                        format(data.startTime()),
                        format(data.stopTime()),
                        Long.toString(Duration.between(data.startTime(), data.stopTime()).toMinutes()),
                        data.notes(),
                        Integer.toString(data.outOfBed())
                    );
    }
}
