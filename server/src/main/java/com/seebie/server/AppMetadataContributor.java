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

        builder.withDetail("app", Map.of("userCount", userRepository.count(), "apiVersion", apiVersion));
    }
}
