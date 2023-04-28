package com.seebie.server;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

import static java.time.Duration.ofDays;
import static java.util.UUID.randomUUID;

@Validated
@ConfigurationProperties(prefix="app")
public record AppProperties(@Positive Integer apiVersion, @NotNull Security security) {

    public record Security(@NotNull RememberMe rememberMe) {
        public record RememberMe(@NotNull Duration tokenValidity, @NotEmpty String key) {}
    }

    public AppProperties() {
        this(1, new Security(new Security.RememberMe(ofDays(30), randomUUID().toString())));
    }

    public AppProperties {
        if(security.rememberMe.tokenValidity.compareTo(Duration.ofDays(90)) > 0) {
            throw new IllegalArgumentException("rememberMeTokenValidity must be less than 90 days");
        }
    }
}
