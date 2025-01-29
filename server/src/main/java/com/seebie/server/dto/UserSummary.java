package com.seebie.server.dto;

import java.util.UUID;

public record UserSummary(String publicId, String displayName) {
    public UserSummary(UUID publicId, String displayName) {
        this(publicId.toString(), displayName);
    }
};
