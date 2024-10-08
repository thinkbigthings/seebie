package com.seebie.server.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ZoneIdValidator.class)
@Target( { ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ZoneIdConstraint {
    String message() default "Invalid zone id";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
