package com.seebie.server.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.net.URLEncoder;

import static java.nio.charset.StandardCharsets.UTF_8;

public class UrlEncodingValidator implements ConstraintValidator<NoUrlEncoding, String> {

    @Override
    public void initialize(NoUrlEncoding value) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext cxt) {
        return URLEncoder.encode(value, UTF_8).equals(value);
    }
}
