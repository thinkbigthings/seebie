package com.seebie.server;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    public void illegalArgument() {

        String exceptionMessage = "message here";
        var response = handler.handleIllegalArgumentException(new IllegalArgumentException(exceptionMessage));

        assertEquals(exceptionMessage, response.getBody().get("message"));
        assertEquals(BAD_REQUEST, response.getStatusCode());
    }

}
