package com.seebie.server;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static com.seebie.server.ApiVersionInterceptor.API_VERSION;
import static com.seebie.server.AppProperties.newAppProperties;
import static org.junit.jupiter.api.Assertions.*;

public class ApiVersionInterceptorTest {

    private final AppProperties properties = newAppProperties(30);
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
