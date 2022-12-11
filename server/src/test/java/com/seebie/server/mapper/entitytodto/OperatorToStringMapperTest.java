package com.seebie.server.mapper.entitytodto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import com.seebie.server.entity.Operator;
import com.seebie.server.mapper.dtotoentity.StringToOperatorMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OperatorToStringMapperTest {

    private OperatorToStringMapper toOpString = new OperatorToStringMapper();
    private StringToOperatorMapper fromString = new StringToOperatorMapper();

    @ParameterizedTest
    @EnumSource(Operator.class)
    void testOperatorStrings(Operator op) {
        assertEquals(op, fromString.apply(toOpString.apply(op)));
    }

    @Test
    public void testOperatorBadStrings() {
        assertThrows(IllegalArgumentException.class, () -> fromString.apply("not an operator"));
    }
}
