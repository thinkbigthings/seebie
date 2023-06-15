package com.seebie.server.dto;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Component
@Documented
@Constraint(validatedBy = ZoneIdValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ZoneIdConstraint {
    String message() default "Invalid zone id";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
