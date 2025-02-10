package com.seebie.server.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MessageTypeTest {

    private final String message = "Enums are stored by ordinal and the order should never be changed";

    @Test
    public void testOrdinalsNeverChange() {

        assertEquals(0, MessageType.ASSISTANT.ordinal(), message);
        assertEquals(1, MessageType.USER.ordinal(), message);
    }
}
