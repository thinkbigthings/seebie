package com.seebie.server;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;
import com.seebie.server.repository.UserRepository;

import java.util.Map;

@Component
public class InfoMetadataContributor implements InfoContributor  {

    private final UserRepository userRepository;
    private final int apiVersion;

    public InfoMetadataContributor(UserRepository userRepository, AppProperties properties) {
        this.userRepository = userRepository;
        this.apiVersion = properties.apiVersion();
    }

    @Override
    public void contribute(Info.Builder builder) {

        var userMetadata = Map.of("count", String.valueOf(userRepository.count()));
        builder.withDetail("users", userMetadata);

        builder.withDetail("apiVersion", apiVersion);
    }
}
