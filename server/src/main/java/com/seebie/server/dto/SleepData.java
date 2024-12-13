package com.seebie.server.dto;

import com.seebie.server.validation.ZoneIdConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDateTime;
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

    public SleepData(String notes, int minutesAwake, LocalDateTime startTime, LocalDateTime stopTime, String zoneId) {
        this(notes, minutesAwake, toZDT(startTime, zoneId), toZDT(stopTime, zoneId), zoneId);
    }

    public SleepData(String notes, int minutesAwake, ZonedDateTime startTime, ZonedDateTime stopTime, String zoneId) {
        this.notes = notes;
        this.minutesAwake = minutesAwake;
        this.startTime = toAlignedZDT(startTime, zoneId);
        this.stopTime = toAlignedZDT(stopTime, zoneId);
        this.zoneId = zoneId;
    }

    private static ZonedDateTime toZDT(LocalDateTime dateTime, String zoneId) {
        return ZonedDateTime.of(dateTime, ZoneId.of(zoneId));
    }

    private static ZonedDateTime toAlignedZDT(ZonedDateTime dateTime, String zoneId) {
        return Optional.ofNullable(dateTime)
                .map(t -> t.truncatedTo(ChronoUnit.MINUTES))
                .map(t -> t.withZoneSameInstant(ZoneId.of(zoneId)))
                .orElse(null);
    }

}
