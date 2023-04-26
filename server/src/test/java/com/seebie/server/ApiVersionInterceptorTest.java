package com.seebie.server;

import org.junit.jupiter.api.Test;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;
import static com.seebie.server.ApiVersionInterceptor.API_VERSION;

public class ApiVersionInterceptorTest {

    private final AppProperties properties = new AppProperties(1);
    private MockHttpServletRequest request = new MockHttpServletRequest();
    private MockHttpServletResponse response = new MockHttpServletResponse();

    private ApiVersionInterceptor versionInterceptor = new ApiVersionInterceptor(properties);

    @Test
    public void missingApiVersionHeader() {

        boolean shouldProceed = versionInterceptor.preHandle(request, response, null);

        assertTrue(shouldProceed);
        assertEquals(properties.apiVersion().toString(), response.getHeader(API_VERSION));
    }

    @Test
    public void matchingApiVersionHeader() {

        request.addHeader(API_VERSION, String.valueOf(properties.apiVersion()));

        boolean shouldProceed = versionInterceptor.preHandle(request, response, null);

        assertTrue(shouldProceed);
        assertEquals(properties.apiVersion().toString(), response.getHeader(API_VERSION));
    }

    @Test
    public void outdatedApiVersionHeader() {

        request.addHeader(API_VERSION, String.valueOf(properties.apiVersion() - 1));

        assertThrows(IncompatibleClientVersionException.class, () -> versionInterceptor.preHandle(request, response, null));

    }
}
