package com.seebie.server.dto;

import com.seebie.server.validation.ZoneIdConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

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
                        @NotNull ZonedDateTime startTime,
                        @NotNull ZonedDateTime stopTime,
                        @ZoneIdConstraint String zoneId)
{

    public SleepData(String notes, int minutesAwake, ZonedDateTime startTime, ZonedDateTime stopTime, String zoneId) {

        this.notes = notes;
        this.minutesAwake = minutesAwake;

        this.startTime = Optional.ofNullable(startTime)
                .map(t -> t.truncatedTo(ChronoUnit.MINUTES))
                .map(t -> t.withZoneSameInstant(ZoneId.of(zoneId)))
                .orElse(null);

        this.stopTime = Optional.ofNullable(stopTime)
                .map(t -> t.truncatedTo(ChronoUnit.MINUTES))
                .map(t -> t.withZoneSameInstant(ZoneId.of(zoneId)))
                .orElse(null);

        this.zoneId = zoneId;
    }

}
