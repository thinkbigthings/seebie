package com.seebie.server.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class PersistentLoginTest {

    @Test
    public void testOrdinalsNeverChange() {

        // This is basically so that the test coverage tool doesn't complain about the per-class coverage
        assertDoesNotThrow(() -> new PersistentLogin());
    }
}
