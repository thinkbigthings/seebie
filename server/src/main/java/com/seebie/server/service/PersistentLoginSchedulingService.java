package com.seebie.server.service;

import com.seebie.server.AppProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * This class and all its components are only used by tests.
 * They're in src/main with the idea that they could be used by an admin feature to manage sessions
 * but that has never been implemented, and it could be fine to move to src/test
 */
@Service
public class PersistentLoginSchedulingService {

    private PersistentLoginExpirationService expirationService;
    private Duration rememberMeTimeout;

    public PersistentLoginSchedulingService(PersistentLoginExpirationService expirationService, AppProperties appProperties) {
        this.expirationService = expirationService;
        this.rememberMeTimeout = appProperties.security().rememberMe().tokenValidity();
    }

    /**
     * Transactional method must be called from a separate class from the Scheduled method.
     */
    @Scheduled(fixedRateString="${app.notification.scanFrequencyMinutes}", timeUnit = TimeUnit.MINUTES)
    public void callDeleteExpiredRememberMeTokens() {
        var timedOut = Instant.now().minus(rememberMeTimeout);
        expirationService.deleteExpiredRememberMeTokens(timedOut);
    }
}
