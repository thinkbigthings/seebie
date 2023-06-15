package com.seebie.server.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RegistrationRequestValidationTest {

    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private static final String username = "username_here";
    private static final String password = "password";
    private static final String email = "x@y.com";

    private static List<Arguments> provideRegistrationArguments() {
        return List.of(
                Arguments.of(new RegistrationRequest(username, password, email), 0),
                Arguments.of(new RegistrationRequest("spaces require encoding", password, email), 1),
                Arguments.of(new RegistrationRequest("spaces require encoding", "", email), 2),
                Arguments.of(new RegistrationRequest("spaces require encoding", "", ""), 3)
        );
    }

    @ParameterizedTest
    @MethodSource("provideRegistrationArguments")
    public void testScanForNotifications(RegistrationRequest data, int numberViolations) {

        // annotations are applied on record components
        // these will be applied on sending to a controller

        var violations = validator.validate(data);

        assertEquals(numberViolations, violations.size());
    }
}
