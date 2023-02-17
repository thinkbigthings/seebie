package com.seebie.server.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = UrlEncodingValidator.class)
@Target( { METHOD, FIELD })
@Retention(RUNTIME)
public @interface NoUrlEncoding {
    String message() default "Value must not require url encoding";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
