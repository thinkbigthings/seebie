package com.seebie.server;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;
import com.seebie.server.repository.UserRepository;

import java.util.Map;

@Component
public class AppMetadataContributor implements InfoContributor  {

    private final UserRepository userRepository;
    private final int apiVersion;

    public AppMetadataContributor(UserRepository userRepository, AppProperties properties) {
        this.userRepository = userRepository;
        this.apiVersion = properties.apiVersion();
    }

    @Override
    public void contribute(Info.Builder builder) {

        var count = Map.of("user", String.valueOf(userRepository.count()));
        var version = Map.of("apiVersion", apiVersion);

        builder.withDetail("app", Map.of("count", count, "version", version));
    }
}
