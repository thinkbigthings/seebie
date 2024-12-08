package com.seebie.server.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 *
 * @param x date only
 * @param y units of hours
 */
public record SleepDataPoint(@NotNull LocalDate x, @PositiveOrZero BigDecimal y) {

    public SleepDataPoint(LocalDateTime stopTime, int minutes, String zoneId) {
        this(stopTime.toLocalDate(),
                BigDecimal.valueOf((double) minutes / 60).setScale(2, RoundingMode.HALF_UP));
    }

}
