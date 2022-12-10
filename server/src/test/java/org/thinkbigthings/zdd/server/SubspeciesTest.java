package org.thinkbigthings.zdd.server;

import org.junit.jupiter.api.Test;
import org.thinkbigthings.zdd.server.entity.Subspecies;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubspeciesTest {

    private final String message = "Enums are stored by ordinal and the order should never be changed";

    @Test
    public void testOrdinalsNeverChange() {

        assertEquals(0, Subspecies.SATIVA.ordinal(), message);
        assertEquals(1, Subspecies.SATIVA_HYBRID.ordinal(), message);
        assertEquals(2, Subspecies.HYBRID.ordinal(), message);
        assertEquals(3, Subspecies.INDICA_HYBRID.ordinal(), message);
        assertEquals(4, Subspecies.INDICA.ordinal(), message);
        assertEquals(5, Subspecies.HIGH_CBD.ordinal(), message);
    }
}
