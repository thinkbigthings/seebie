package com.seebie.server;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix="app")
public record AppProperties(@NotNull Security security, @NotNull Notification notification, @NotNull AI ai) {

    public record Notification(@NotNull TriggerAfter triggerAfter, @NotNull int scanFrequencyMinutes) {
        public record TriggerAfter(@NotNull Duration lastNotified, @NotNull Duration sleepLog) {}
    }

    public record Security(@NotNull RememberMe rememberMe) {
        public record RememberMe(@NotNull Duration tokenValidity, @NotEmpty String key, @NotNull @Positive int scanFrequencyMinutes) {
            public int tokenValiditySeconds() {
                return (int) tokenValidity.toSeconds();
            }
        }
    }

    public record AI(@NotNull System system) {
        public record System(@NotNull String prompt) {
        }
    }

}
