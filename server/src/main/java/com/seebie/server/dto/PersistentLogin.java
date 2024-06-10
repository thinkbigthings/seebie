package com.seebie.server.dto;

import java.time.Instant;


public record PersistentLogin(String series, String token, String username, Instant lastUsed) {
}
