package com.seebie.server.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.unmodifiableSet;

/**
 *
 * @param notes
 * @param minutesAwake
 * @param tags
 * @param startTime
 * @param stopTime
 * @param zoneId A zone id parsable by ZoneId.of() Also see https://www.iana.org/time-zones
 */
public record SleepData(@NotNull String notes,
                        @PositiveOrZero int minutesAwake,
                        @NotNull Set<String> tags,
                        @NotNull ZonedDateTime startTime,
                        @NotNull ZonedDateTime stopTime,
                        @ZoneIdConstraint String zoneId)
{

    public SleepData(String notes, int minutesAwake, ZonedDateTime startTime, ZonedDateTime stopTime, String zoneId) {
        this(notes, minutesAwake, new HashSet<>(), startTime, stopTime, zoneId);
    }

    public SleepData(String notes, int minutesAwake, Set<String> tags, ZonedDateTime startTime, ZonedDateTime stopTime, String zoneId) {
        this.notes = notes;
        this.minutesAwake = minutesAwake;
        this.tags = unmodifiableSet(tags);
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.zoneId = zoneId;
    }

}
