package com.seebie.server.dto;

import com.seebie.server.validation.ZoneIdConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static com.seebie.server.mapper.entitytodto.LocalDateTimeConverter.toZDT;

/**
 *
 * @param notes
 * @param minutesAwake
 * @param startTime
 * @param stopTime
 * @param zoneId A zone id parsable by ZoneId.of() Also see https://www.iana.org/time-zones
 */
public record SleepData(@NotNull String notes,
                        @PositiveOrZero int minutesAwake,
                        @NotNull LocalDateTime startTime,
                        @NotNull LocalDateTime stopTime,
                        @ZoneIdConstraint String zoneId)
{

    public SleepData {
        startTime = Optional.ofNullable(startTime).map(t -> t.truncatedTo(ChronoUnit.MINUTES)).orElse(null);
        stopTime = Optional.ofNullable(stopTime).map(t -> t.truncatedTo(ChronoUnit.MINUTES)).orElse(null);
    }

    public int minutesAsleep() {
        return (int) Duration.between(toZDT(startTime, zoneId), toZDT(stopTime, zoneId)).abs().toMinutes() - minutesAwake;
    }

}
