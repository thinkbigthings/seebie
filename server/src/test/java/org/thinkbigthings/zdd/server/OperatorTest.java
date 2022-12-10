package org.thinkbigthings.zdd.server;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.thinkbigthings.zdd.server.entity.Operator.*;

public class OperatorTest {

    private final String message = "Enums are stored by ordinal and the order should never be changed";

    @Test
    public void testOrdinalsNeverChange() {
        assertEquals(0, LT.ordinal(), message);
        assertEquals(1, LTE.ordinal(), message);
        assertEquals(2, EQ.ordinal(), message);
        assertEquals(3, GTE.ordinal(), message);
        assertEquals(4, GT.ordinal(), message);
    }

}
