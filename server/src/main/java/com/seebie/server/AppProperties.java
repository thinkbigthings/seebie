package com.seebie.server;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

import static java.time.Duration.ofDays;
import static java.time.Duration.ofMinutes;
import static java.util.UUID.randomUUID;

@Validated
@ConfigurationProperties(prefix="app")
public record AppProperties(@Positive Integer apiVersion, @NotNull Security security, @NotNull Notification notification) {

    public record Notification(boolean enabled, @NotNull TriggerAfter triggerAfter) {
        public record TriggerAfter(@NotNull Duration lastNotified, @NotNull Duration sleepLog) {}
    }

    public record Security(@NotNull RememberMe rememberMe) {
        public record RememberMe(@NotNull Duration tokenValidity, @NotEmpty String key) {
            public int tokenValiditySeconds() {
                return (int) tokenValidity.toSeconds();
            }
        }
    }

    public AppProperties {
        if(security.rememberMe.tokenValidity.compareTo(Duration.ofDays(90)) > 0) {
            throw new IllegalArgumentException("rememberMeTokenValidity must be less than 90 days");
        }
    }

}
