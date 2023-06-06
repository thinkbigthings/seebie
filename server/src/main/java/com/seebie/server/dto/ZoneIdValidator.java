package com.seebie.server.dto;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ZoneIdValidator implements ConstraintValidator<ZoneIdConstraint, String> {

    private String[] allowedZoneIds;

    @Override
    public void initialize(ZoneIdConstraint constraint) {
        allowedZoneIds = ZoneIds.ALL;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        // Null values are NOT considered valid
        if (value == null) {
            return false;
        }

        for (String zoneId : allowedZoneIds) {
            if (zoneId.equals(value)) {
                return true;
            }
        }

        return false;
    }
}
