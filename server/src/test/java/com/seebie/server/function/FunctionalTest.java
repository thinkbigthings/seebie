package com.seebie.server.function;

import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FunctionalTest {

    @Test
    public void testConstructable() {
        assertDoesNotThrow(Functional::new);
    }

    @Test
    public void testUncheckExceptionConsumer() {

        var consumer = Functional.uncheck((String s) -> {
            if(s.isEmpty()) {
                throw new ParseException("Empty string", 0);
            }
        });

        assertThrows(RuntimeException.class, () -> consumer.accept(""));
    }

    @Test
    public void testUncheckExceptionFunction() {

        var dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        var parser = Functional.uncheck((String s) -> dateFormat.parse(s));

        assertThrows(RuntimeException.class, () -> parser.apply("abc"));
    }
}
