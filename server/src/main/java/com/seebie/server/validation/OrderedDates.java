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
@Constraint(validatedBy = OrderedDatesValidator.class)
@Target( {ElementType.PARAMETER, METHOD, FIELD })
@Retention(RUNTIME)
public @interface OrderedDates {
    String message() default "Dates must be in order";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
