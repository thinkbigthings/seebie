package com.seebie.server.validation;

import com.seebie.server.dto.SleepData;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Duration;


public class ValidDurationsValidator implements ConstraintValidator<ValidDurations, SleepData> {

    @Override
    public void initialize(ValidDurations value) {
    }

    @Override
    public boolean isValid(SleepData value, ConstraintValidatorContext cxt) {
        if (value == null || value.startTime() == null || value.stopTime() == null) {
            return false;
        }
        return value.minutesAwake() < Duration.between(value.startTime(), value.stopTime()).toMinutes();
    }
}
