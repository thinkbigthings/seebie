package com.seebie.server.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = ValidDurationsValidator.class)
@Target( {ElementType.PARAMETER, METHOD, FIELD })
@Retention(RUNTIME)
public @interface ValidDurations {
    String message() default "Time awake should be less than sleep session duration";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
